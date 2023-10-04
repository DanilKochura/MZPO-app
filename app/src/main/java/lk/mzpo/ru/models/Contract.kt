package lk.mzpo.ru.models

import CoursePreview
import com.google.gson.annotations.SerializedName

data class Contract (

        @SerializedName("id"              ) var id             : Int,
        @SerializedName("user_id"         ) var userId         : Int ,
        @SerializedName("course_id"       ) var courseId       : Int,
        @SerializedName("org_id"          ) var orgId          : Int?    = null,
        @SerializedName("contract_name"   ) var contractName   : String,
        @SerializedName("number"          ) var number         : String,
        @SerializedName("contract_number" ) var contractNumber : String,
        @SerializedName("study_form"      ) var studyForm      : String? = null,
        @SerializedName("price"           ) var price          : Int,
        @SerializedName("discount"        ) var discount       : Int,
        @SerializedName("debt"            ) var debt           : Int?    = null,
        @SerializedName("total"           ) var total          : Int,
        @SerializedName("uid"             ) var uid            : String? = null,
        @SerializedName("order_date"      ) var orderDate      : String? = null,
        @SerializedName("close_date"      ) var closeDate      : String? = null,
        @SerializedName("progress"        ) var progress       : Progress? = null,
        @SerializedName("extended"        ) var extended       : String? = null,
        @SerializedName("date_cancel"     ) var dateCancel     : String? = null,
        @SerializedName("why_cancel"      ) var whyCancel      : String? = null,
        @SerializedName("amount"          ) var amount         : String? = null,
        @SerializedName("status"          ) var status         : Int? = null,
        @SerializedName("course"          ) var course         : CoursePreview? = null


    )