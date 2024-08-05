package lk.mzpo.ru.screens

import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.navigation.NavHostController
import androidx.navigation.ui.navigateUp
import com.google.gson.Gson
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import io.sanghun.compose.video.VideoPlayer
import io.sanghun.compose.video.cache.VideoPlayerCacheManager
import io.sanghun.compose.video.controller.VideoPlayerControllerConfig
import io.sanghun.compose.video.uri.VideoPlayerMediaItem
import lk.mzpo.ru.R
import lk.mzpo.ru.models.study.ActiveMaterials
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.PasswordTextField
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.VideoViewModel
import retrofit2.http.Url
import java.io.InputStream
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(
    navHostController: NavHostController, video: ActiveMaterials, contract: Int, videoViewModel: VideoViewModel = viewModel()
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

        VideoPlayer(
            mediaItems = listOf(
                VideoPlayerMediaItem.NetworkMediaItem(
                    url = "https://lk.mzpo-s.ru/build/videos/${video.activeFile!!.upload}",
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
                if (it/1000 - state.value  > 20 ||  video.activeFile!!.size!!.toFloat() - it/1000f < 10)
                {
//                    Log.e("CurrentTime", it.div(1000).toFloat().toString())
                    state.value = it/1000F
                    videoViewModel.update(ctx, video.id!!, it/1000.toFloat(), video.activeFile!!.size!!.toFloat(), video.activeFile!!.size!!.toFloat(), contract)
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

