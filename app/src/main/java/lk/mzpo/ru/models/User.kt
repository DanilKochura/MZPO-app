package lk.mzpo.ru.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id"   ) val id: Int,
    @SerializedName("name"   ) val name: String,
    @SerializedName("email"   ) val email: String,
    @SerializedName("userData"   ) val userData: UserData? = null
)
