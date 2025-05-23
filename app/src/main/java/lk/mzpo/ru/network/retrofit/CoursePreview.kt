
import com.google.gson.annotations.SerializedName


data class CoursePreview (

	@SerializedName("id") val id : Int,
	@SerializedName("image") val image : String,
	@SerializedName("name") val name : String,
	@SerializedName("prefix") val prefix : String,
	@SerializedName("hours") val hours : Int,
	@SerializedName("new_prices") val prices : NewPrices,
	@SerializedName("uid") val uid : String,
	@SerializedName("category") val category : Int,
	@SerializedName("doctype") val doctype : String,
	@SerializedName("is_dist") val IsDist : Boolean
) {
	fun isDist(): Boolean {
		return this.IsDist
	}

	fun getMinPrice(): NewPrice
	{
		if (this.isDist())
		{
			return this.prices.dist!!
		} else
		{
			if (this.prices.sale15 != null && this.prices.sale15.price != 0)
			{
				return this.prices.sale15
			} else if (this.prices.weekend != null && this.prices.weekend.price != 0)
			{
				return this.prices.weekend
			} else if (this.prices.intensive != null && this.prices.intensive.price != 0)
			{
				return this.prices.intensive
			} else if (this.prices.ind != null && this.prices.ind.price != 0)
			{
				return this.prices.ind
			}
			return NewPrice(0,0);
		}
	}

	fun getMinPriceType(): String
	{
		if (this.isDist())
		{
			return "Дистанционно"
		} else
		{
			if (this.prices.sale15 != null && this.prices.sale15.price != 0)
			{
				return "Очно"
			} else if (this.prices.weekend != null && this.prices.weekend.price != 0)
			{
				return "Выходные"
			} else if (this.prices.intensive != null && this.prices.intensive.price != 0)
			{
				return "Интенсив"
			} else if (this.prices.ind != null && this.prices.ind.price != 0)
			{
				return "Индивидуально"
			}
			return "";
		}
	}
}