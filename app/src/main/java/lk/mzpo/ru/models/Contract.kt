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
        @SerializedName("notPassed"        ) var notPassed       : NotPasssed? = null,
        @SerializedName("extendPrice"        ) var extendPrice       : Int? = null,
        @SerializedName("debt"            ) var debt           : Int    = 0,
        @SerializedName("total"           ) var total          : Int,
        @SerializedName("uid"             ) var uid            : String? = null,
        @SerializedName("docs_errors"     ) var docs_errors    : Int = 0,
        @SerializedName("order_date"      ) var orderDate      : String? = null,
        @SerializedName("legal"      ) var legal      : String = "0",
        @SerializedName("close_date"      ) var closeDate      : String? = null,
        @SerializedName("progress"        ) var progress       : Progress? = null,
        @SerializedName("extended"        ) var extended       : String? = null,
        @SerializedName("date_cancel"     ) var dateCancel     : String? = null,
        @SerializedName("why_cancel"      ) var whyCancel      : String? = null,
        @SerializedName("amount"          ) var amount         : String? = null,
        @SerializedName("status"          ) var status         : Int? = null,
        @SerializedName("course"          ) var course         : CoursePreview? = null,
        @SerializedName("certs"           ) var certs          : List<String> = emptyList(),
        @SerializedName("need_docs"       ) var need_docs      : Boolean = false,
        @SerializedName("practiceData"  ) var practiceData   : PracticeData? = null,
        @SerializedName("examData"  ) var examData   : List<ExamData> = emptyList()


    )

data class PracticeData(
        @SerializedName("duration"       ) var duration       : Int?                      = null,
        @SerializedName("blanks"         ) var blanks         : ArrayList<Blank>         = arrayListOf(),
        @SerializedName("payed-practice" ) var courses : ArrayList<CoursePreview> = arrayListOf()
)


data class Blank(
        @SerializedName("file"       )  val `file`: String,
        @SerializedName("path"       ) val path: String
)
data class PracticeOchno(
        @SerializedName("text"       )  val text: String? = null,
        @SerializedName("phone"       ) val phone: String? = null,
        @SerializedName("blank"       ) val blank: String? = null
)


data class NotPasssed (

        @SerializedName("phone_for_notPaid" ) var phoneForNotPaid : String? = null,
        @SerializedName("module_id"         ) var moduleId        : Int?    = null,
        @SerializedName("module_uid"        ) var moduleUid       : String? = null,
        @SerializedName("free"              ) var free            : String? = null,
        @SerializedName("exams"             ) var exams           : Array<String> = arrayOf(),
        @SerializedName("extend_till"       ) var extendTill      : String? = null,
        @SerializedName("left"              ) var left            : String? = null,
        @SerializedName("extendTimes"       ) var extendTimes     : String? = null,
        @SerializedName("status"            ) var status          : Int?    = null

)



data class Gift (

        @SerializedName("id"        ) var id       : Int?    = null,
        @SerializedName("name"      ) var name     : String? = null,
        @SerializedName("image"     ) var image    : String? = null,
        @SerializedName("date_from" ) var dateFrom : String? = null,
        @SerializedName("date_to"   ) var dateTo   : String? = null,
        @SerializedName("status"    ) var status   : String? = null,
        @SerializedName("user_type" ) var userType : String? = null

)

data class ExamData (

        @SerializedName("id"               ) var id             : Int?    = null,
        @SerializedName("contract_id"      ) var contractId     : Int?    = null,
        @SerializedName("module_id"        ) var moduleId       : Int?    = null,
        @SerializedName("module_passed"    ) var modulePassed   : String? = null,
        @SerializedName("exam"             ) var exam           : String? = null,
        @SerializedName("passed"           ) var passed         : String? = null,
        @SerializedName("passed_at"        ) var passedAt       : String? = null,
        @SerializedName("extend_till"      ) var extendTill     : String? = null,
        @SerializedName("extended"         ) var extended       : Int?    = null,
        @SerializedName("free_extend"      ) var freeExtend     : String? = null,
        @SerializedName("can_free_extend"  ) var canFreeExtend  : String? = null,
        @SerializedName("exam_ticket"      ) var examTicket     : String? = null,
//        @SerializedName("exam_answer"      ) var examAnswer     : Obj? = null,
        @SerializedName("exam_answered_at" ) var examAnsweredAt : String? = null,
        @SerializedName("exam_passed"      ) var examPassed     : String? = null,
        @SerializedName("exam_answer_till" ) var examAnswerTill : String? = null,
        @SerializedName("status"           ) var status         : String? = null,
        @SerializedName("payed_access"     ) var payedAccess    : String? = null,
        @SerializedName("warning"          ) var warning        : String? = null,
        @SerializedName("email_sent"       ) var emailSent      : String? = null,
        @SerializedName("created_at"       ) var createdAt      : String? = null,
        @SerializedName("updated_at"       ) var updatedAt      : String? = null

)

fun Contract.canAccessCourse(): Boolean {
        return notPassed == null && status!! in intArrayOf(1, 6, 7, 9, 10, 11, 14)
}

fun Contract.isActiveCourse(): Boolean {
        return status!! in intArrayOf(1, 6, 7, 8, 9, 10, 11, 14)
}

fun Contract.isCompleted(): Boolean {
        return status!! in intArrayOf(0, 3, 15)
}

fun Contract.isCanceled(): Boolean {
        return status!! in intArrayOf(2, 5)
}

fun Contract.isSuspended(): Boolean {
        return status == 8
}

fun Contract.canExtendAccess(): Boolean {
        return status!! in intArrayOf(1, 6, 7, 9, 10, 11, 17)
}

fun Contract.hasFreeExtension(): Boolean {
        return notPassed?.free?.isNotEmpty() == true && notPassed?.free != "0"
}

fun Contract.hasPaidExtension(): Boolean {
        return notPassed?.free == "0"
}