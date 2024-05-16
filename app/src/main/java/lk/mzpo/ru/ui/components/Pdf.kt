import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL



//class PdfViewModel(private val pdfUrl: String, private val context: Context) {
//    var pageCount by mutableStateOf(0)
//        private set
//
//    private var pdfRenderer: PdfRenderer? = null
//
//    init {
//        loadPdf()
//    }
//
//    private fun loadPdf() {
//        viewModelScope.launch {
//            try {
//                pdfRenderer = withContext(Dispatchers.IO) {
//                    PdfRenderer(fetchPdfFromUrl(pdfUrl, context))
//                }
//                pageCount = pdfRenderer!!.pageCount
//            } catch (e: IOException) {
//                // Обработка ошибок при загрузке PDF
//            }
//        }
//    }
//
//    @Throws(IOException::class)
//    private suspend fun fetchPdfFromUrl(urlString: String, context: Context): ParcelFileDescriptor {
//        val url = URL(urlString)
//        val connection = url.openConnection() as HttpURLConnection
//        connection.connect()
//        val inputStream = connection.inputStream
//        val file = File(context.cacheDir, "temp.pdf")
//        inputStream.use { input ->
//            file.outputStream().use { output ->
//                input.copyTo(output)
//            }
//        }
//        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
//    }
//
//    fun renderPageToBitmap(pageIndex: Int): Bitmap? {
//        return pdfRenderer?.let { pdfRenderer ->
//            val page = pdfRenderer.openPage(pageIndex)
//            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
//            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//            page.close()
//            bitmap
//        }
//    }
//}
//
//@Composable
//fun PdfViewer(viewModel: PdfViewModel) {
//    val pageCount = viewModel.pageCount
//    for (pageIndex in 0 until pageCount) {
//        val bitmap = remember(pageIndex) {
//            viewModel.renderPageToBitmap(pageIndex)
//        }
//        bitmap?.let {
//            Image(
//                bitmap = it.asImageBitmap(),
//                contentDescription = null,
//                modifier = Modifier.fillMaxSize().padding(8.dp)
//            )
//        }
//    }
//}