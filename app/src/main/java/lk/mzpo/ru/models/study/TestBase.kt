package lk.mzpo.ru.models.study

import com.google.gson.annotations.SerializedName


data class TestBase (

    @SerializedName("questionsCount"        ) var questionsCount        : Int?    = null,
    @SerializedName("final"                 ) var final                 : Boolean?    = null,
    @SerializedName("attemptsLeft"          ) var attemptsLeft          : Int? = null,
    @SerializedName("hasNotFinishedAttempt" ) var hasNotFinishedAttempt : Boolean? = null,
    @SerializedName("material"              ) var material              : ActiveMaterials? = null,
    @SerializedName("question"              ) var question              : Question? = null,

)