package lk.mzpo.ru.models.study

import com.google.gson.annotations.SerializedName


data class Answer (

    @SerializedName("id"          ) var id         : Int?    = null,
    @SerializedName("question_id" ) var questionId : Int?    = null,
    @SerializedName("answer"      ) var answer     : String? = null,
    @SerializedName("image"      ) var image     : String? = null,
    @SerializedName("comparison"  ) var comparison : String? = null,
    @SerializedName("correct"     ) var correct    : String? = null,
    @SerializedName("position"    ) var position   : Int?    = null,
    @SerializedName("status"      ) var status     : String? = null

)