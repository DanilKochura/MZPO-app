
import com.google.gson.annotations.SerializedName


data class CoursePreview (

	@SerializedName("id") val id : Int,
	@SerializedName("image") val image : String,
	@SerializedName("name") val name : String,
	@SerializedName("prefix") val prefix : String,
	@SerializedName("hours") val hours : Int,
	@SerializedName("prices") val prices : Prices,
	@SerializedName("uid") val uid : String,
	@SerializedName("category") val category : Int,
	@SerializedName("doctype") val doctype : String
) {
	fun isDist(): Boolean {
		if (this.prices.dist != 0 && this.prices.dist != null)
		{
			return true;
		}
		return false
	}

	fun getMinPrice(): Int
	{
		if (this.isDist())
		{
			return this.prices.dist!!
		} else
		{
			if (this.prices.sale15 != 0 && this.prices.sale15 != null)
			{
				return this.prices.sale15
			} else if (this.prices.weekend != 0 && this.prices.weekend != null)
			{
				return this.prices.weekend
			} else if (this.prices.intensive != 0 && this.prices.intensive != null)
			{
				return this.prices.intensive
			} else if (this.prices.ind != 0 && this.prices.ind != null)
			{
				return this.prices.ind
			}
			return 0;
		}
	}

	fun getMinPriceType(): String
	{
		if (this.isDist())
		{
			return "Дистанционно"
		} else
		{
			if (this.prices.sale15 != 0 && this.prices.sale15 != null)
			{
				return "Очно"
			} else if (this.prices.weekend != 0 && this.prices.weekend != null)
			{
				return "Выходные"
			} else if (this.prices.intensive != 0 && this.prices.intensive != null)
			{
				return "Интенсив"
			} else if (this.prices.ind != 0 && this.prices.ind != null)
			{
				return "Индивидуально"
			}
			return "";
		}
	}
}