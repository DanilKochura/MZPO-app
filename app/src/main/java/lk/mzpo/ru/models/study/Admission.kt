package lk.mzpo.ru.models.study

import com.google.gson.annotations.SerializedName


data class Admission (

    @SerializedName("id"     ) var id     : Int?    = null,
    @SerializedName("name"   ) var name   : String? = null,
    @SerializedName("uid"    ) var uid    : String? = null,
    @SerializedName("status" ) var status : Int?    = null,
    @SerializedName("pivot"  ) var pivot  : Pivot?  = Pivot()

)

data class Pivot (

    @SerializedName("course_id"    ) var courseId    : Int? = null,
    @SerializedName("admission_id" ) var admissionId : Int? = null,
    @SerializedName("required" ) var required : String? = null,
    @SerializedName("for_access" ) var for_access : String? = null

)

data class UserDocument (


    @SerializedName("id"            ) var id           : Int?       = null,
    @SerializedName("order_id"      ) var orderId      : Int?       = null,
    @SerializedName("admission_id"  ) var admissionId  : Int?       = null,
    @SerializedName("file"          ) var file         : String?    = null,
    @SerializedName("sent_by_email" ) var sentByEmail  : String?    = null,
    @SerializedName("doc_condition" ) var docCondition : String?    = null,
    @SerializedName("comment"       ) var comment      : String?    = null,
    @SerializedName("created_at"    ) var createdAt    : String?    = null,
    @SerializedName("updated_at"    ) var updatedAt    : String?    = null,
    @SerializedName("admission"     ) var admission    : Admission? = Admission()

)