import com.google.gson.annotations.SerializedName




data class CoursePreview (

	@SerializedName("id") val id : Int,
	@SerializedName("image") val image : String,
	@SerializedName("name") val name : String,
	@SerializedName("prefix") val prefix : String,
	@SerializedName("hours") val hours : Int,
	@SerializedName("prices") val prices : Prices
)