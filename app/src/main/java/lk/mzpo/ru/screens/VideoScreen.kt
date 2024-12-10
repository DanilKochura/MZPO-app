package lk.mzpo.ru.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
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
import lk.mzpo.ru.models.study.ActiveMaterials
import lk.mzpo.ru.models.study.NewMaterials
import lk.mzpo.ru.viewModel.VideoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(
    navHostController: NavHostController, video: NewMaterials, contract: Int, videoViewModel: VideoViewModel = viewModel()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val state = remember {
            mutableStateOf(0F)
        }
        val ctx = LocalContext.current
//        val pdfState = rememberVerticalPdfReaderState(
//            resource = ResourceType.Remote("https://lk.mzpo-s.ru/build/course_files/1/1_2.pdf"),
//            isZoomEnable = true,
//            isAccessibleEnable = true
//        )
//        VerticalPDFReader(
//            state = pdfState,
//            modifier = Modifier
//                .fillMaxSize()
//                .background(color = Color.Gray)
//
//        )
        VideoPlayerCacheManager.initialize(ctx, 1024 * 1024 * 10)    // 10Mb

//        Log.d("TEST", "https://lk.mzpo-s.ru/build/videos/${video.file!!.upload}")
        VideoPlayer(
            mediaItems = listOf(
                VideoPlayerMediaItem.NetworkMediaItem(
                    url = "https://lk.mzpo-s.ru/build/videos/${video.file!!.upload}",
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
                if (it/1000 - state.value  > 20 ||  video.file!!.size!!.toFloat() - it/1000f < 10)
                {
//                    Log.e("CurrentTime", it.div(1000).toFloat().toString())
                    state.value = it/1000F
                    var send = it/1000.toFloat()
                    if (video.file!!.size!!.toFloat() - it/1000f < 10)
                    {
                        send = video.file!!.size!!.toFloat()
                    }
                    videoViewModel.update(ctx, video.id, send, video.id)
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
                ActiveMaterials::class.java
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

