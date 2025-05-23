package lk.mzpo.ru.models


data class Prices (
    var sale15: Int?,
    val dist: Int?,
    val ind: Int?,
    val weekend: Int?,
    val intensive: Int?
        )

data class PricesNew (
    var sale15: PriceNew?,
    val dist: PriceNew?,
    val ind: PriceNew?,
    val weekend: PriceNew?,
    val intensive: PriceNew?
)

data class PriceNew (
    var old: Int,
    var price: Int
)