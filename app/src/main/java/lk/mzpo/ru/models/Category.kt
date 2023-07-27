package lk.mzpo.ru.models

data class Category(val name: String, val url: String, val child: List<Category>? = null)
