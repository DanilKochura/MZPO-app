package lk.mzpo.ru.network.retrofit;

import com.google.gson.annotations.SerializedName;

data class Feedback (
    @SerializedName("subject") val subject: String,
    @SerializedName("message") val message: String,
    @SerializedName("parent")  val parent: Int?,
    @SerializedName("type")  val type: Int,
    @SerializedName("mobile")  val mobile: String = "android"
        )
