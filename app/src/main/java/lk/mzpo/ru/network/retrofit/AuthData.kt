package lk.mzpo.ru.network.retrofit

import com.google.gson.annotations.SerializedName

data class AuthData(
    @SerializedName("email") val email: String,
    @SerializedName("passrowd") val passrowd: String,
    @SerializedName("device_name")  val device_name: String
)
