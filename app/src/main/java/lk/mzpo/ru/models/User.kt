package lk.mzpo.ru.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id"   ) val id: Int,
    @SerializedName("name"   ) val name: String,
    @SerializedName("email"   ) val email: String,
    @SerializedName("phone"   ) val phone: String,
    @SerializedName("id_1c"   ) val id_1c: String,
    @SerializedName("avatar"   ) val avatar: String? = null,
    @SerializedName("job_access"   ) val jobAccess: Int? = null,
)
