package lk.mzpo.ru.models.study

import com.google.gson.annotations.SerializedName


data class NewMaterials (

    @SerializedName("id"                ) var id              : Int          = 0,
    @SerializedName("user_id"           ) var userId          : Int?           = null,
    @SerializedName("contract_id"       ) var contractId      : Int?           = null,
    @SerializedName("study_module_id"   ) var studyModuleId   : Int?           = null,
    @SerializedName("study_material_id" ) var studyMaterialId : Int?           = null,
    @SerializedName("file_id"           ) var fileId          : Int?           = null,
    @SerializedName("file_size"         ) var fileSize        : Int?           = null,
    @SerializedName("watched"           ) var watched         : Int           = 0,
    @SerializedName("percent"           ) var percent         : Int?           = null,
    @SerializedName("passed"            ) var passed          : String?        = null,
    @SerializedName("module_passed"     ) var modulePassed    : String?        = null,
    @SerializedName("created_at"        ) var createdAt       : String?        = null,
    @SerializedName("updated_at"        ) var updatedAt       : String?        = null,
    @SerializedName("study_module"      ) var studyModule     : NewStudyModule?   = NewStudyModule(),
    @SerializedName("study_material"    ) var studyMaterial   : NewStudyMaterial? = NewStudyMaterial(),
    @SerializedName("file"              ) var file            : NewFile?          = NewFile()

)

data class NewStudyModule (

    @SerializedName("id"              ) var id            : Int?    = null,
    @SerializedName("study_course_id" ) var studyCourseId : Int?    = null,
    @SerializedName("name"            ) var name          : String? = null,
    @SerializedName("_lft"            ) var Lft           : Int?    = null,
    @SerializedName("_rgt"            ) var Rgt           : Int?    = null,
    @SerializedName("parent_id"       ) var parentId      : String? = null,
    @SerializedName("status"          ) var status        : String? = null

)

data class NewStudyMaterial (

    @SerializedName("id"              ) var id            : Int?    = null,
    @SerializedName("study_module_id" ) var studyModuleId : Int?    = null,
    @SerializedName("file_id"         ) var fileId        : Int?    = null,
    @SerializedName("name"            ) var name          : String? = null,
    @SerializedName("type"            ) var type          : String? = null,
    @SerializedName("position"        ) var position      : Int?    = null,
    @SerializedName("status"          ) var status        : String? = null,
    @SerializedName("created_at"      ) var createdAt     : String? = null,
    @SerializedName("updated_at"      ) var updatedAt     : String? = null

)

data class NewFile (

    @SerializedName("id"              ) var id             : Int?    = null,
    @SerializedName("name"            ) var name           : String? = null,
    @SerializedName("type"            ) var type           : String? = null,
    @SerializedName("upload"          ) var upload         : String? = null,
    @SerializedName("image"           ) var image          : String? = null,
    @SerializedName("size"            ) var size           : Int?    = null,
    @SerializedName("closed_contract" ) var closedContract : String? = null,
    @SerializedName("position"        ) var position       : Int?    = null,
    @SerializedName("status"          ) var status         : String? = null,
    @SerializedName("created_at"      ) var createdAt      : String? = null,
    @SerializedName("updated_at"      ) var updatedAt      : String? = null

)