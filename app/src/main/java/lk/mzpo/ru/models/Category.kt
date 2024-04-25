package lk.mzpo.ru.models

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("alias") val url: String,
    @SerializedName("parent") val parent: Int,
    @SerializedName("children") val child: List<Category>? = null,
    @SerializedName("image") val image: String = "",
    @SerializedName("amount") val amount: Int = 0
)
