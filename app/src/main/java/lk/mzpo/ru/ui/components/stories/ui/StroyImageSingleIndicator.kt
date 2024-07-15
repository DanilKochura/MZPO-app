package lk.mzpo.ru.ui.components.stories.ui

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import lk.mzpo.ru.ui.components.stories.Story
import lk.mzpo.ru.ui.components.stories.data.Indicator


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryImageSingleIndicator(
    modifier: Modifier,
    indicator: Indicator,
    state: PagerState,
    size: Int,
    currentImageUrl: String = "",
    story: Story? = null,
    swipeTiming: Int,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    stories: MutableTransitionState<Boolean> = MutableTransitionState(false)
) {

    HorizontalPager(
        userScrollEnabled = false, state = state, modifier = modifier
    ) {

        StoryImageSingleIndicator(
            url = currentImageUrl,
            indicator = indicator,
            swipeTiming = swipeTiming,
            onLeftClick = onLeftClick,
            onRightClick = onRightClick,
            story = story,
        )
    }
}

@Composable
fun StoryImageSingleIndicator(
    url: String,
    story: Story? = null,
    indicator: Indicator,
    swipeTiming: Int,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,

    ) {

    val animatable = remember(url) {
        Animatable(0.0f)
    }

    LaunchedEffect(key1 = url) {
        animatable.animateTo(1.0f, animationSpec = tween(swipeTiming, easing = LinearEasing))
        onRightClick()
    }

    Box(modifier = Modifier.fillMaxSize())
    {
        StoryImage(animatable.value, url, indicator, swipeTiming, onLeftClick, onRightClick)
        story?.button?.invoke()
        Log.d("MyLogStory", "testtst")
    }

}

@Composable
fun StoryImage(
    progress: Float,
    url: String,
    indicator: Indicator,
    swipeTiming: Int,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit
) {

    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels

    Box(modifier = Modifier.fillMaxSize()) {
        StoryAsyncImage(url, screenWidth, onLeftClick, onRightClick)
        LinearProgressIndicator(
            progress = progress,
            modifier = indicator.modifier,
            color = indicator.indicatorColor,
            trackColor = indicator.indicatorTrackColor
        )
    }
}