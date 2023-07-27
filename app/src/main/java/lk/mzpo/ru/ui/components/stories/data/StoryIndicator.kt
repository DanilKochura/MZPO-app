package lk.mzpo.ru.ui.components.stories.data

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lk.mzpo.ru.ui.components.stories.IndicatorType
import lk.mzpo.ru.ui.components.stories.data.Indicator

object StoryIndicator {

    fun singleIndicator(
        indicatorColor: Color = Color.White,
        indicatorTrackColor: Color = Color.Gray,
        modifier: Modifier = Modifier.fillMaxWidth()
    ) = Indicator(
        IndicatorType.Single, indicatorColor, indicatorTrackColor, modifier
    )

    fun multiIndicator(
        indicatorColor: Color = Color.White,
        indicatorTrackColor: Color = Color.Gray,
        modifier: Modifier = Modifier.fillMaxWidth(),
        indicatorPadding: PaddingValues = PaddingValues(),
        indicatorSpacing: Dp = 4.dp
    ) = Indicator(
        type = IndicatorType.Multiple, indicatorColor, indicatorTrackColor, modifier, indicatorPadding, indicatorSpacing
    )
}
