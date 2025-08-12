package lk.mzpo.ru.screens

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.navigation.NavHostController
import com.google.gson.Gson
import io.sanghun.compose.video.VideoPlayer
import io.sanghun.compose.video.cache.VideoPlayerCacheManager
import io.sanghun.compose.video.controller.VideoPlayerControllerConfig
import io.sanghun.compose.video.uri.VideoPlayerMediaItem
import lk.mzpo.ru.models.study.NewMaterials
import lk.mzpo.ru.viewModel.VideoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(
    navHostController: NavHostController, video: NewMaterials, contract: Int, videoViewModel: VideoViewModel = viewModel()
) {
    val ctx = LocalContext.current

    val powerManager = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
    val wakeLock = remember { powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "VideoScreen:WakeLock") }

    // Запрещаем автоблокировку при старте воспроизведения
    LaunchedEffect(Unit) {
        Log.d("MyWakeLog", "test")
        wakeLock.acquire(100*60*1000L /*100 minutes*/)  // Захватываем блокировку экрана
    }

    DisposableEffect(Unit) {
        val activity = ctx as? android.app.Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    // Освобождаем блокировку, когда видео завершено или экран закрывается
    DisposableEffect(Unit) {
        onDispose {
            wakeLock.release()  // Освобождаем блокировку экрана
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        val state = remember {
            mutableStateOf(0F)
        }
        VideoPlayerCacheManager.initialize(ctx, 1024 * 1024 * 100)    // 10Mb
        if (video.file == null)
        {
            Toast.makeText(ctx, "Произошла ошибка!", Toast.LENGTH_SHORT).show()
            navHostController.navigateUp()
        }
        VideoPlayer(
            mediaItems = listOf(
                VideoPlayerMediaItem.NetworkMediaItem(
                    url = "https://trayektoriya.ru/build/videos/${video.file!!.upload}",
                    mediaMetadata = MediaMetadata.Builder().setTitle("Widevine DASH cbcs: Tears").build(),
                    mimeType = MimeTypes.APPLICATION_M3U8,
                )
            ),
            handleLifecycle = true,
            autoPlay = true,
            usePlayerController = true,
            enablePip = false,
            handleAudioFocus = true,
            controllerConfig = VideoPlayerControllerConfig(
                showSpeedAndPitchOverlay = true,
                showSubtitleButton = false,
                showCurrentTimeAndTotalTime = true,
                showBufferingProgress = false,
                showForwardIncrementButton = true,
                showBackwardIncrementButton = true,
                showBackTrackButton = false,
                showNextTrackButton = false,
                showRepeatModeButton = false,
                controllerShowTimeMilliSeconds = 5_000,
                controllerAutoShow = true,
                showFullScreenButton = true
            ),
            volume = 0.5f,  // volume 0.0f to 1.0f
            onCurrentTimeChanged = { // long type, current player time (millisec)
                val time = it/1000
                if (time - state.value  > 30)
                {
                    state.value = time.toFloat()
                    var send = time.toFloat()
                    if (video.file!!.size!!.toFloat() - time.toFloat() < 40)
                    {
                        send = video.file!!.size!!.toFloat()
                    }
                    val size = video.file!!.size!!.toFloat()
                    val percent = ((send/size)*100).toInt()
                    var passed = if (percent > 97) 1 else 0

                    Log.d("MyLog", "Watched: "+send.toString())
                    Log.d("MyLog", "Size: "+size.toString())
                    Log.d("MyLog", "Percent: "+percent.toString())
                    Log.d("MyLog", "Progress: "+video.id.toString())
                    Log.d("MyLog", "Passed: "+passed.toString())


                    videoViewModel.update(ctx, video.id, send.toInt(), percent, passed)
                }
            },
            playerInstance = {
//                Log.d("MyLog", "aaaa")// ExoPlayer instance (Experimental)
            },

            modifier = Modifier
                .fillMaxSize(),

        )
        IconButton(onClick = {
            val gson = Gson()
            val video_ = gson.toJson(
                video,
                NewMaterials::class.java
            )

            navHostController.currentBackStackEntry?.savedStateHandle?.set(
                "video",
                video_
            )
            navHostController.currentBackStackEntry?.savedStateHandle?.set(
                "contract",
                contract.toString()
            )

            navHostController.navigateUp()
//            navHostController.navigate("study")
                             }, modifier = Modifier
            .padding(20.dp)
            .align(Alignment.TopEnd)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "", tint = Color.White)
        }
    }

}

