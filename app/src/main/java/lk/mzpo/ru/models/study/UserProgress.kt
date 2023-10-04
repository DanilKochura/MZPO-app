package lk.mzpo.ru.models.study

import com.google.gson.annotations.SerializedName

data class UserProgress (

    @SerializedName("id"         ) var id        : Int?    = null,
    @SerializedName("user_id"    ) var userId    : Int?    = null,
    @SerializedName("file_id"    ) var fileId    : Int?    = null,
    @SerializedName("progress"   ) var progress  : Int?    = null,
    @SerializedName("viewed"     ) var viewed    : String? = null,
    @SerializedName("created_at" ) var createdAt : String? = null,
    @SerializedName("updated_at" ) var updatedAt : String? = null

)