package lk.mzpo.ru.models.study

import com.google.gson.annotations.SerializedName

data class ActiveFile (

    @SerializedName("id"              ) var id             : Int?          = null,
    @SerializedName("name"            ) var name           : String?       = null,
    @SerializedName("type"            ) var type           : String?       = null,
    @SerializedName("upload"          ) var upload         : String?       = null,
    @SerializedName("image"           ) var image          : String?       = null,
    @SerializedName("size"            ) var size           : Int?          = null,
    @SerializedName("closed_contract" ) var closedContract : String?       = null,
    @SerializedName("position"        ) var position       : Int?          = null,
    @SerializedName("status"          ) var status         : String?       = null,
    @SerializedName("created_at"      ) var createdAt      : String?       = null,
    @SerializedName("updated_at"      ) var updatedAt      : String?       = null,
    @SerializedName("user_progress"   ) var userProgress   : UserProgress? = UserProgress()

)