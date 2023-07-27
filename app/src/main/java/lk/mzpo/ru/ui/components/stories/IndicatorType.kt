package lk.mzpo.ru.ui.components.stories

sealed class IndicatorType {
    object Single: IndicatorType()
    object Multiple: IndicatorType()
}