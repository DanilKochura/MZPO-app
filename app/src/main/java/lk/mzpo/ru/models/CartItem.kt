package lk.mzpo.ru.models

import CoursePreview
import com.google.gson.annotations.SerializedName

class CartItem(
  @SerializedName("id")  val id: Int,
  @SerializedName("type")  val type: String,
  @SerializedName("date")  val date: String,
  @SerializedName("course_id")  val course_id: Int,
  @SerializedName("course")  val course: CoursePreview,
)