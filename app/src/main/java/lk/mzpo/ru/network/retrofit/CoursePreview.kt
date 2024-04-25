import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import com.google.gson.annotations.SerializedName
import lk.mzpo.ru.ui.theme.Aggressive_red


data class CoursePreview (

	@SerializedName("id") val id : Int,
	@SerializedName("image") val image : String,
	@SerializedName("name") val name : String,
	@SerializedName("prefix") val prefix : String,
	@SerializedName("hours") val hours : Int,
	@SerializedName("prices") val prices : Prices,
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
}