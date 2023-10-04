package lk.mzpo.ru.models.study

import com.google.gson.annotations.SerializedName


data class StudyModule (

    @SerializedName("id"               ) var id              : Int?                       = null,
    @SerializedName("study_course_id"  ) var studyCourseId   : Int?                       = null,
    @SerializedName("name"             ) var name            : String?                    = null,
    @SerializedName("_lft"             ) var Lft             : Int?                       = null,
    @SerializedName("_rgt"             ) var Rgt             : Int?                       = null,
    @SerializedName("parent_id"        ) var parentId        : String?                    = null,
    @SerializedName("status"           ) var status          : String?                    = null,
    @SerializedName("active_materials" ) var activeMaterials : ArrayList<ActiveMaterials> = arrayListOf(),

)