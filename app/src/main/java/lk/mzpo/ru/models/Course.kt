package lk.mzpo.ru.models

import NewPrices
import com.google.gson.annotations.SerializedName

class Course(
    val id: Int,
    val category: Int,
    val name: String,
    val prefix: String,
    val hours: Int,
    val prices: NewPrices,
    val description: String,
    val uid: String,
    val images: List<String>,
    val groups: List<GroupCart>?,
    val docs: List<Document>,
    val admissions: List<String>,
    val modules: List<Module>,
    val doctype: String,
)

data class CourseReview(
    @SerializedName("text"       ) var text        : String? = null,
    @SerializedName("rate"       ) var rate        : Int? = null,
    @SerializedName("user"       ) var user        : String,
    @SerializedName("date"       ) var date        : String,
    @SerializedName("user_avatar") var user_avatar : String? = null
)