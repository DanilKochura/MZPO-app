package lk.mzpo.ru.models

class Course(
    val id: Int,
    val category: Int,
    val name: String,
    val prefix: String,
    val hours: Int,
    val prices: Prices,
    val description: String,
    val uid: String,
    val images: List<String>,
    val groups: List<Group>?,
    val docs: List<Document>,
    val admissions: List<String>,
    val modules: List<Module>,
    val doctype: String,
)