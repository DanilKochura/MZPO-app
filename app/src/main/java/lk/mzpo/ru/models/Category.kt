package lk.mzpo.ru.models

data class Category(val id: Int, val name: String, val url: String, val parent: Int, val child: List<Category>? = null, val amount: Int = 0)
