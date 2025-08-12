package lk.mzpo.ru.screens

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lk.mzpo.ru.R
import lk.mzpo.ru.network.retrofit.SaveLastPageService
import lk.mzpo.ru.ui.components.LoadableScreen
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.FileViewModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfScreen_New(
    navHostController: NavHostController,
    fileViewModel: FileViewModel = viewModel(),
    materialId: Int,
    moduleId: Int,
    contractId: Int,
) {
    val context = LocalContext.current
    fileViewModel.getData(context, contractId, moduleId, materialId)

    LoadableScreen(loaded = fileViewModel.loaded, error = fileViewModel.error)
    {
        val material = fileViewModel.material.value!!
        val startPage = material.watched
        val pdfUrl = "https://trayektoriya.ru/${material.file?.upload}"
        // Разрешаем поворот экрана
        DisposableEffect(Unit) {
            val activity = context as? android.app.Activity
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            onDispose {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        val pref = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val token_ = pref.getString("token_lk", "")

        // Состояния PDF
        var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
        var currentPage by remember { mutableStateOf(startPage.coerceAtLeast(0)) } // Используем startPage
        var pageCount by remember { mutableStateOf(0) }
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }

        val scope = rememberCoroutineScope()
        var scale by remember { mutableFloatStateOf(1f) }
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }


        // Функция рендеринга страницы
        fun renderPage(pageIndex: Int) {
            pdfRenderer?.let { renderer ->
                if (pageIndex in 0 until renderer.pageCount) {
                    isLoading = true

                    scope.launch {
                        try {
                            renderer.openPage(pageIndex).use { page ->
                                val scaleFactor = 2.0f
                                val width = (page.width * scaleFactor).toInt()
                                val height = (page.height * scaleFactor).toInt()
                                val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                                    page.render(this, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                }

                                withContext(Dispatchers.Main) {
                                    bitmap?.recycle()
                                    bitmap = newBitmap
                                    currentPage = pageIndex
                                    isLoading = false
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                error = "Ошибка рендеринга страницы"
                                isLoading = false
                            }
                        }
                    }
                }
            }
            Log.d("MyLog", currentPage.toString()+" "+material.watched.toString())
            if (currentPage > material.watched - 2 || currentPage == 0) {
                Log.d("MyLog", "HERE "+currentPage.toString()+" "+material.id.toString())
                SaveLastPageService().send(
                    "Bearer " + token_?.trim('"'),
                    SaveLastPageService.PostBody(
                        progress = material.id,
                        page =  if (material.file?.size!! - currentPage < 3) material.file?.size!! else currentPage
                    )

                ).enqueue(object : retrofit2.Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("API Request", "I got an error and i don't know why :(")
                        Log.e("API Request", t.message.toString())
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        Log.d("API Request", response.body().toString())
                        Log.d("API Request", response.message())
                        Log.d("API Request", response.errorBody().toString())
                        Log.d("API Request", response.raw().body.toString())
                        if (response.isSuccessful) {
                            material.watched = currentPage
                        }
                    }
                })
            }
            Log.d("MyLog", currentPage.toString()+' '+material.file?.size.toString())
            if (currentPage == material.file?.size!! - 2)
            {
                Toast.makeText(context, "Вы просмотрели документ полностью!", Toast.LENGTH_SHORT).show()
            }
        }
        fun changePage(delta: Int) {
            val newPage = (currentPage + delta).coerceIn(0, pageCount - 1)
            if (newPage != currentPage) {
                scale = 1f
                offsetX = 0f
                offsetY = 0f
                renderPage(newPage)
            }

        }



        // Загрузка PDF
        LaunchedEffect(pdfUrl) {
            withContext(Dispatchers.IO) {
                try {
                    val tempFile = File.createTempFile("pdf", ".pdf", context.cacheDir).apply {
                        deleteOnExit()
                    }

                    URL(pdfUrl).openStream().use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }

                    val pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                    val renderer = PdfRenderer(pfd)
                    pdfRenderer = renderer
                    pageCount = renderer.pageCount
                    renderPage(startPage.coerceIn(0, renderer.pageCount - 1)) // Рендерим стартовую страницу
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        error = e.message ?: "Ошибка загрузки PDF"
                        isLoading = false
                    }
                }
            }
        }

        // Смена страницы


        // Очистка ресурсов
        DisposableEffect(Unit) {
            onDispose {
                bitmap?.recycle()
                pdfRenderer?.close()
            }
        }

        // UI
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("${"Документ"}") },
                    navigationIcon = {
                        IconButton(
                            onClick = { navHostController.navigateUp() },
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Назад",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.height(80.dp)
                )
            },
            bottomBar = {
                if (pageCount > 1) {
                    BottomAppBar(
                        containerColor = Primary_Green,
                        modifier = Modifier.height(50.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { changePage(-1) },
                                enabled = currentPage > 0
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_keyboard_arrow_left_24),
                                    contentDescription = "Предыдущая",
                                    tint = Color.White
                                )
                            }

                            Text(
                                text = "${currentPage + 1}/$pageCount",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )

                            IconButton(
                                onClick = {
                                    changePage(1)

                                },
                                enabled = currentPage < pageCount - 1
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_keyboard_arrow_right_24),
                                    contentDescription = "Следующая",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                when {
                    isLoading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Загрузка страницы...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    error != null -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                error!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { renderPage(currentPage) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("Повторить попытку")
                            }
                        }
                    }
                    bitmap != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        // Ограничиваем масштаб: минимальный 1.0, максимальный 5.0
                                        scale = (scale * zoom).coerceIn(1f, 5f)
                                        offsetX = (offsetX + pan.x).coerceIn(-500f, 500f)
                                        offsetY = (offsetY + pan.y).coerceIn(-500f, 500f)
                                    }
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White)
                                    .align(Alignment.Center)
                            ) {
                                Image(
                                    bitmap = bitmap!!.asImageBitmap(),
                                    contentDescription = "PDF страница",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer(
                                            scaleX = scale,
                                            scaleY = scale,
                                            translationX = offsetX,
                                            translationY = offsetY
                                        )
                                )
                            }
                        }
                    }
                }

                // Кнопки масштабирования
                if (bitmap != null) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        FloatingActionButton(
                            onClick = { scale = (scale + 0.5f).coerceAtMost(5f) },
                            modifier = Modifier.size(48.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_zoom_in_24),
                                contentDescription = "Увеличить"
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        FloatingActionButton(
                            onClick = {
                                // Уменьшаем, но не меньше 1.0
                                scale = (scale - 0.5f).coerceAtLeast(1f)
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_zoom_out_24),
                                contentDescription = "Уменьшить"
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        FloatingActionButton(
                            onClick = {
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_zoom_out_map_24),
                                contentDescription = "Сбросить"
                            )
                        }
                    }
                }
            }
        }
    }
}