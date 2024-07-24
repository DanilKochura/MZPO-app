package lk.mzpo.ru.models.study

import com.google.gson.annotations.SerializedName


data class Question (

    @SerializedName("id"       ) var id       : Int?    = null,
    @SerializedName("file_id"  ) var fileId   : Int?    = null,
    @SerializedName("label"    ) var label    : String? = null,
    @SerializedName("question" ) var question : String? = null,
    @SerializedName("file"     ) var file     : String? = null,
    @SerializedName("multiple" ) var multiple : String? = null,
    @SerializedName("position" ) var position : Int?    = null,
    @SerializedName("status"   ) var status   : String? = null,
    @SerializedName("active_answers" ) var activeAnswers : ArrayList<Answer> = arrayListOf()
)


data class TestResult (

    @SerializedName("questionsCount"        ) var questionsCount      : Int?                           = null,
    @SerializedName("attempt"               ) var attempt             : Int?                           = null,
    @SerializedName("final"                 ) var final               : Boolean?                       = null,
    @SerializedName("result"                ) var result              : Float

)


data class TestAttempt (

    @SerializedName("id"                ) var id              : Int?    = null,
    @SerializedName("user_id"           ) var userId          : Int?    = null,
    @SerializedName("order_id"          ) var orderId         : Int?    = null,
    @SerializedName("study_material_id" ) var studyMaterialId : Int?    = null,
    @SerializedName("attempt"           ) var attempt         : Int?    = null,
    @SerializedName("result"            ) var result          : Float? = null,
    @SerializedName("created_at"        ) var createdAt       : String? = null,
    @SerializedName("updated_at"        ) var updatedAt       : String? = null

)