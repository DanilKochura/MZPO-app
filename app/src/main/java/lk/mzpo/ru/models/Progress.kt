package lk.mzpo.ru.models

import com.google.gson.annotations.SerializedName



data class Progress (

        @SerializedName("video" ) var video : String? = null,
        @SerializedName("files" ) var files : String? = null,
        @SerializedName("tests" ) var tests : String? = null,
        @SerializedName("total" ) var total : Int? = null

)