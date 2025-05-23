package lk.mzpo.ru.models.study

import CoursePreview
import com.google.gson.annotations.SerializedName


data class CourseSchedule (

    @SerializedName("id"            ) var id           : Int?    = null,
    @SerializedName("order_id"      ) var orderId      : Int?    = null,
    @SerializedName("module_id"     ) var moduleId     : Int?    = null,
    @SerializedName("group_id"      ) var groupId      : Int?    = null,
    @SerializedName("passed"        ) var passed       : String? = null,
    @SerializedName("course_closed" ) var courseClosed : String? = null,
    @SerializedName("status"        ) var status       : String? = null,
    @SerializedName("module"        ) var  module       : CoursePreview? = null,
    @SerializedName("group"         ) var group        : Group  = Group(),
)

data class Teacher (

    @SerializedName("id"          ) var id          : Int?    = null,
    @SerializedName("name"        ) var name        : String? = null,
    @SerializedName("image"       ) var image       : String? = null,
    @SerializedName("description" ) var description : String? = null,
    @SerializedName("uid"         ) var uid         : String? = null,
    @SerializedName("alias"       ) var alias       : String? = null,
    @SerializedName("status"      ) var status      : Int?    = null

)

data class AllSchedules (

    @SerializedName("id"         ) var id        : Int?     = null,
    @SerializedName("group_id"   ) var groupId   : Int?     = null,
    @SerializedName("date"       ) var date      : String?  = null,
    @SerializedName("time_start" ) var timeStart : String?  = null,
    @SerializedName("time_end"   ) var timeEnd   : String?  = null,
    @SerializedName("territory"  ) var territory : String?  = null,
    @SerializedName("auditory"   ) var auditory  : String?  = null,
    @SerializedName("teacher_id" ) var teacherId : Int?     = null,
    @SerializedName("uid"        ) var uid       : String?  = null,
    @SerializedName("status"     ) var status    : String?  = null,
    @SerializedName("teacher"    ) var teacher   : Teacher? = Teacher()

)

data class Group (

    @SerializedName("id"            ) var id           : Int?                    = null,
    @SerializedName("course_id"     ) var courseId     : Int?                    = null,
    @SerializedName("title"         ) var title        : String?                 = null,
    @SerializedName("start_date"    ) var startDate    : String?                 = null,
    @SerializedName("end_date"      ) var endDate      : String?                 = null,
    @SerializedName("exam"          ) var exam         : String?                 = null,
    @SerializedName("group_type"    ) var groupType    : String?                 = null,
    @SerializedName("is_usual"      ) var isUsual      : String?                 = null,
    @SerializedName("is_weekend"    ) var isWeekend    : String?                 = null,
    @SerializedName("is_intensive"  ) var isIntensive  : String?                 = null,
    @SerializedName("is_evening"    ) var isEvening    : String?                 = null,
    @SerializedName("uid"           ) var uid          : String?                 = null,
    @SerializedName("all_schedules" ) var allSchedules : ArrayList<AllSchedules> = arrayListOf()

)

data class UserSchedule(
    @SerializedName("course"          ) var course         : String,
    @SerializedName("course_pref"     ) var course_pref    : String,
    @SerializedName("date"            ) var date           : String,
    @SerializedName("time"            ) var time           : String,
    @SerializedName("teacher"         ) var teacher        : String,
    @SerializedName("time_start"      ) var timeStart      : String,
    @SerializedName("auditory"        ) var auditory       : String,
    @SerializedName("teacher_image"   ) var teacher_image  : String? = null
)


data class PassedModules (

    @SerializedName("id"            ) var id           : Int?    = null,
    @SerializedName("order_id"      ) var orderId      : Int?    = null,
    @SerializedName("module_id"     ) var moduleId     : Int?    = null,
    @SerializedName("group_id"      ) var groupId      : String? = null,
    @SerializedName("passed"        ) var passed       : String? = null,
    @SerializedName("course_closed" ) var courseClosed : String? = null,
    @SerializedName("status"        ) var status       : String? = null,
    @SerializedName("module"        ) var module       : String? = null

)