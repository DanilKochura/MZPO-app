
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.sqrt

// Manage Imports according to your use



@Composable
fun AppPdfViewer(
    modifier: Modifier = Modifier,
    url: String,
    fileName: String,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    onClose: () -> Unit
) {

    var file: File? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(key1 = Unit) {
        file = async { downloadAndGetFile(url, fileName) }.await()
    }

    if (file == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            Loader()
                CircularProgressIndicator()
        }
    } else {
        val rendererScope = rememberCoroutineScope()
        val mutex = remember { Mutex() }
        val renderer by produceState<PdfRenderer?>(null, file) {
            rendererScope.launch(Dispatchers.IO) {
                val input = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                value = PdfRenderer(input)
            }
            awaitDispose {
                val currentRenderer = value
                rendererScope.launch(Dispatchers.IO) {
                    mutex.withLock {
                        currentRenderer?.close()
                    }
                }
            }
        }
        val context = LocalContext.current
        val imageLoader = LocalContext.current.imageLoader
        val imageLoadingScope = rememberCoroutineScope()
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.onSecondary)
//                .aspectRatio(1f / sqrt(2f))
        ) {

            val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
            val height = (width * sqrt(2f)).toInt()
            val pageCount by remember(renderer) { derivedStateOf { renderer?.pageCount ?: 0 } }

            var scale by rememberSaveable {
                mutableFloatStateOf(1f)
            }
            var offset by remember {
                mutableStateOf(Offset.Zero)
            }
            val state =
                rememberTransformableState { zoomChange, panChange, rotationChange ->
                    scale = (scale * zoomChange).coerceIn(1f, 5f)

                    val extraWidth = (scale - 1) * constraints.maxWidth
                    val extraHeight = (scale - 1) * constraints.maxHeight

                    val maxX = extraWidth / 2
                    val maxY = extraHeight / 2

                    offset = Offset(
                        x = (offset.x + scale * panChange.x).coerceIn(-maxX, maxX),
                        y = (offset.y + scale * panChange.y).coerceIn(-maxY, maxY),
                    )
                }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationX = offset.y
                    }
                    .transformable(state),
                verticalArrangement = verticalArrangement
            ) {
                items(
                    count = pageCount,
                    key = { index -> "${file!!.name}-$index" }
                ) { index ->
                    val cacheKey = MemoryCache.Key("${file!!.name}-$index")
                    val cacheValue: Bitmap? = imageLoader.memoryCache?.get(cacheKey)?.bitmap

                    var bitmap: Bitmap? by remember { mutableStateOf(cacheValue) }
                    if (bitmap == null) {
                        DisposableEffect(file, index) {
                            val job = imageLoadingScope.launch(Dispatchers.IO) {
                                val destinationBitmap =
                                    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                                mutex.withLock {
                                    if (!coroutineContext.isActive) return@launch
                                    try {
                                        renderer?.let {
                                            it.openPage(index).use { page ->
                                                page.render(
                                                    destinationBitmap,
                                                    null,
                                                    null,
                                                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                                )
                                            }
                                        }
                                    } catch (e: Exception) {
                                        //Just catch and return in case the renderer is being closed
                                        return@launch
                                    }
                                }
                                bitmap = destinationBitmap
                            }
                            onDispose {
                                job.cancel()
                            }
                        }
                        Box(
                            modifier = Modifier
                                .background(Color.White)
                                .fillMaxWidth()
                        )
                    } else {
                        val request = ImageRequest.Builder(context)
                            .size(width, height)
                            .memoryCacheKey(cacheKey)
                            .data(bitmap)
                            .build()

                        Image(
                            modifier = Modifier
                                .background(Color.Transparent)
                                .border(1.dp, MaterialTheme.colors.background)
//                                .aspectRatio(1f / sqrt(2f))
                                .fillMaxSize(),
                            contentScale = ContentScale.Fit,
                            painter = rememberAsyncImagePainter(request),
                            contentDescription = "Page ${index + 1} of $pageCount"
                        )
                    }
                }
            }


            IconButton(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.TopStart),
                onClick = onClose
            ) {
                Icon(imageVector = Icons.Rounded.Close, contentDescription = null, tint = Color.Black)
            }

            TextButton(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.TopEnd),
                onClick = {
                    context.sharePdf(file!!)
                },
            ) {
                Text(
                    modifier = Modifier
                        .padding(vertical = 7.dp, horizontal = 15.dp),
                    text = "Подеиться",
//                    style = newTitleStyle(fontSize = 14.sp, color = Teal)
                )
            }
        }
    }
}



fun Context.sharePdf(file: File) {
    val uri = Uri.fromFile(file)

    val shareIntent = Intent()
    shareIntent.setAction(Intent.ACTION_SEND)
    shareIntent.setType("application/pdf")
    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
    this.startActivity(Intent.createChooser(shareIntent, "Share via"))
}

fun isFileExist(path: String): Boolean {
    val file = File(path)
    return file.exists()
}

fun getUniqueFileName(url: String): String {
    val fileName = URL(url).toURI().path.replace("/", "_")
    // Hash the filename to create a shorter and more unique identifier
    val hash = fileName.hashCode().toString(Character.MAX_RADIX)
    // Combine a sanitized filename with a hash for uniqueness
    return "$fileName-$hash"
}

suspend fun downloadAndGetFile(url: String, fileName: String): File? {
    if (isFileExist(fileName)) return File(fileName)
    val connection: HttpURLConnection? = null
    var file: File? = null
    try {
        withContext(Dispatchers.IO) {
            val connectionOne = URL(url).openConnection() as HttpURLConnection
            connectionOne.connect()

            if (connectionOne.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext null
            }

            val inputStream = connectionOne.inputStream
            file = File.createTempFile(fileName, ".pdf")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            outputStream.close()
        }
    } catch (e: IOException) {
//        AppLogger.error("File", "ErrorMessage " + e.message + ", ${e.cause}")
    } finally {
        connection?.disconnect()
    }
    return file
}

suspend fun downloadAndRenderPdf(url: String): List<Bitmap>? {
    val connection: HttpURLConnection? = null
    var parcelFileDescriptor: ParcelFileDescriptor? = null
    var pdfRenderer: PdfRenderer? = null
    var bitmaps: MutableList<Bitmap>? = null

    try {
        withContext(Dispatchers.IO) {
            val connectionOne = URL(url).openConnection() as HttpURLConnection
            connectionOne.connect()

            if (connectionOne.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext null
            }

            val inputStream = connectionOne.inputStream
            val outputFile = File.createTempFile("temp_pdf", ".pdf")
            val outputStream = FileOutputStream(outputFile)
            inputStream.copyTo(outputStream)
            outputStream.close()
            parcelFileDescriptor =
                ParcelFileDescriptor.open(outputFile, ParcelFileDescriptor.MODE_READ_ONLY)
            parcelFileDescriptor?.let {
                pdfRenderer = PdfRenderer(it)
                val pageCount = pdfRenderer!!.pageCount
                bitmaps = mutableListOf()
                for (i in 0 until pageCount) {
                    pdfRenderer?.let { renderer ->
//                        AppLogger.e("pdfRenderer   $i   $pageCount")
                        val page = renderer.openPage(i)
                        val bitmap =
                            Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmaps?.add(bitmap)
                        page.close()
                    }
                }
            }
        }
    } catch (e: IOException) {
        // Handle exception
    } finally {
        connection?.disconnect()
        parcelFileDescriptor?.close()
        pdfRenderer?.close()
    }

    return bitmaps
}
