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