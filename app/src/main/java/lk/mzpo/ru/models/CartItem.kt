package lk.mzpo.ru.models

import CoursePreview
import com.google.gson.annotations.SerializedName

class CartItem(
  @SerializedName("id")  val id: Int,
  @SerializedName("type")  val type: String,
  @SerializedName("org_id")  val org_id: Int,
  @SerializedName("price")  var  price: Int,
  @SerializedName("date")  val date: String?,
  @SerializedName("group_id")  val group_id: Int?,
  @SerializedName("group")  val group: GroupCart?,
  @SerializedName("purchase_type")  val purchase_type: String?,
  @SerializedName("course_id")  val course_id: Int,
  @SerializedName("course")  val course: CoursePreview,
  @SerializedName("priceNew")  val priceCart: PriceCart,
  @SerializedName("dist_dates")  val dist_dates: ArrayList<String> = ArrayList(),
  @SerializedName("sale")  val sale: Int  = 0,
)

class PriceCart(
  @SerializedName("full_price")  val full_price: Int,
  @SerializedName("price")  val price: Int,
  @SerializedName("sale")  val sale: Int
)