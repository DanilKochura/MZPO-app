package lk.mzpo.ru.ui.components.stories.ui

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import lk.mzpo.ru.R
import kotlin.math.log

@Composable
fun StoryAsyncImage(url: String,
                    screenWidth: Int,
                    onLeftClick: () -> Unit,
                    onRightClick: () -> Unit) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        placeholder = painterResource(id = R.drawable.placehold),
        contentDescription = "Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {

                    if (it.x < screenWidth / 2) {
                        onLeftClick()
                    } else {
                        onRightClick()
                    }
                }
//                detectDragGestures { change, dragAmount ->
//                    Log.d("MyLog1", change.scrollDelta.x.toString())
//                }

            }
    )
}