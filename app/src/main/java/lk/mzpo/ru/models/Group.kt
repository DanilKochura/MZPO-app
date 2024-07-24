package lk.mzpo.ru.models


import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalTime

data class Group(
   val id: Int,
   val course_id: Int,
   val title: String,
   val start: LocalDate,
   val end: LocalDate,
   val time: LocalTime,
//   val is_usual: Int,
//   val is_weekend: Int,
//   val is_intensive: Int,
//   val is_evening: Int,
   val uid: String,
   val teacher: String,
   val teacher_name: String = "Не указан",
   val month: Int = 0
)

data class GroupCart(
   @SerializedName("id") val id: Int,
   @SerializedName("course_id") val course_id: Int,
   @SerializedName("group_type") val group_type: Int,
   @SerializedName("title") val title: String,
   @SerializedName("start_date") val start: String,
   @SerializedName("end_date") val end: String,
   @SerializedName("time") val time: String,
//   val is_usual: Int,
//   val is_weekend: Int,
//   val is_intensive: Int,
//   val is_evening: Int,
   @SerializedName("uid")  val uid: String,
   @SerializedName("teacher") val teacher: String,
   @SerializedName("teacher_name") val teacher_name: String = "Не указан",
   @SerializedName("month") val month: Int = 0
)