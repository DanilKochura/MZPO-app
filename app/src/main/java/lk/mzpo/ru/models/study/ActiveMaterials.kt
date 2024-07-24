package lk.mzpo.ru.models.study

import com.google.gson.annotations.SerializedName


data class ActiveMaterials (

    @SerializedName("id"              ) var id            : Int?        = null,
    @SerializedName("study_module_id" ) var studyModuleId : Int?        = null,
    @SerializedName("file_id"         ) var fileId        : Int?        = null,
    @SerializedName("name"            ) var name          : String?     = null,
    @SerializedName("position"        ) var position      : Int?        = null,
    @SerializedName("status"          ) var status        : String?     = null,
    @SerializedName("created_at"      ) var createdAt     : String?     = null,
    @SerializedName("attempts"        ) var attempts     : ArrayList<TestAttempt>     = arrayListOf(),
    @SerializedName("updated_at"      ) var updatedAt     : String?     = null,
    @SerializedName("active_file"     ) var activeFile    : ActiveFile? = ActiveFile(),
    @SerializedName("user_progress"   ) var userProgress   : UserProgressCourse? = UserProgressCourse()


)


data class UserProgressCourse (

    @SerializedName("id"         ) var id        : Int?    = null,
    @SerializedName("user_id"    ) var userId    : Int?    = null,
    @SerializedName("file_id"    ) var fileId    : Int?    = null,
    @SerializedName("progress"   ) var progress  : Int?    = null,
    @SerializedName("viewed"     ) var viewed    : String? = null,
    @SerializedName("created_at" ) var createdAt : String? = null,
    @SerializedName("updated_at" ) var updatedAt : String? = null

)