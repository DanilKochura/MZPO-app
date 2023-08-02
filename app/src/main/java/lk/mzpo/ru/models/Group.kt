package lk.mzpo.ru.models


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