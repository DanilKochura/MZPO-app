package lk.mzpo.ru.network.retrofit

data class Data2Amo(
    val comment: String,
    val email: String,
    val form_name_site: String,
    val name: String,
    val phone: String,
    val event: Boolean = false,
    val event_name: String = "",
    val pipeline: Int = 3198184
)
