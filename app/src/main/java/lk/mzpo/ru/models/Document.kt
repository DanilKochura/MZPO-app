package lk.mzpo.ru.models

import com.google.gson.annotations.SerializedName

data class Document(
    val name: String,
    val image: String
)
data class Certificate (

    @SerializedName("id"       ) var id      : Int?    = null,
    @SerializedName("doc_name" ) var docName : String? = null,
    @SerializedName("number"   ) var number  : String? = null,
    @SerializedName("course"   ) var course  : String? = null,
    @SerializedName("status"   ) var status  : String? = null,
    @SerializedName("debt"   ) var debt  : Int? = null,
    @SerializedName("digital"  ) var digital : Int?    = null,
    @SerializedName("image"    ) var image   : String? = null,
    @SerializedName("date"    ) var date   : String? = null,
    @SerializedName("date_given"    ) var date_given   : String? = null

)