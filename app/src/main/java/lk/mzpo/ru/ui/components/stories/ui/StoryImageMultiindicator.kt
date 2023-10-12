package lk.mzpo.ru.ui.components.stories.ui

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lk.mzpo.ru.MainActivity
import lk.mzpo.ru.ui.components.stories.Story
import lk.mzpo.ru.ui.components.stories.data.Indicator
import lk.mzpo.ru.ui.components.stories.data.ScrollState


@Composable
fun rememberStoryState(size: Int): Array<ScrollState> {
    return remember {
        Array(size) { ScrollState() }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryImageMultipleIndicator(url: String,
                                story: Story? = null,
                                size: Int,
                                indicator: Indicator,
                                pagerState: PagerState,
                                swipeTime: Int,
                                scrollStateList: Array<ScrollState>,
                                onLeftClick: () -> Unit,
                                onRightClick: () -> Unit,
                                onSwitch: () -> Unit,
                                flag: MutableTransitionState<Boolean> = MutableTransitionState(false),
                                paused: MutableState<Boolean>,
                                exit: MutableTransitionState<Boolean>
) {
    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels
    val ctx = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {

        LaunchedEffect(key1 = paused.value)
        {
            val page = pagerState.currentPage
            scrollStateList[page].pauseScroll.value =  paused.value
        }

        HorizontalPager(
            userScrollEnabled = false,
            pageCount = size,
            state = pagerState) {
            StoryAsyncImage(url, screenWidth, onLeftClick, onRightClick)
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
                story?.button?.invoke()
            }
        }
        Row(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(indicator.indicatorSpacing),
            verticalAlignment = Alignment.CenterVertically) {
            repeat(size) {
                ProgressLine(
                    scrollState = scrollStateList[it],
                    indicator = indicator,
                    swipeTime = swipeTime,
                    onSwitch = onSwitch,
                    paused = paused
                )

            }
            IconButton(onClick = {
                exit.targetState = false

            }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "", tint = Color.White)
            }
        }

    }
}


@Composable
fun RowScope.ProgressLine(
    scrollState: ScrollState,
    indicator: Indicator,
    swipeTime: Int,
    onSwitch: () -> Unit,
    paused: MutableState<Boolean>
) {
    val animatable = scrollState.animatable

    LaunchedEffect(key1 = scrollState.startScroll.value) {
        if (scrollState.startScroll.value) {
            animatable.animateTo(1.0f, animationSpec = tween(swipeTime, easing = LinearEasing))
            onSwitch()
        } else {
            animatable.snapTo(0f)
        }
    }


    LaunchedEffect(key1 = scrollState.immediateFill) {
        if (scrollState.immediateFill.value) {
            if (animatable.isRunning) {
                animatable.stop()
            }
            animatable.snapTo(1f)
            scrollState.immediateFill.value = false
        }
    }
    val ctx = LocalContext.current
    LaunchedEffect(key1 = scrollState.pauseScroll.value)
    {
        if (scrollState.pauseScroll.value)
        {
//            Toast.makeText(ctx, "Stopped", Toast.LENGTH_SHORT).show()
            animatable.stop()
        } else
        {
//            Toast.makeText(ctx, "Started", Toast.LENGTH_SHORT).show()
            animatable.animateTo(1f, animationSpec = tween(swipeTime, easing = LinearEasing))
        }

    }

    LinearProgressIndicator(
        progress = animatable.value,
        modifier = Modifier
            .weight(1f)
            .then(indicator.modifier),
        color = indicator.indicatorColor,
        trackColor = indicator.indicatorTrackColor
    )
}
