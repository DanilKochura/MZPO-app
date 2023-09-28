package lk.mzpo.ru.viewModel

import CoursePreview
import Prices
import android.R.attr.password
import android.accounts.AccountManager.KEY_PASSWORD
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lk.mzpo.ru.R
import lk.mzpo.ru.models.Category
import lk.mzpo.ru.models.Course
import lk.mzpo.ru.network.retrofit.Data2Amo
import lk.mzpo.ru.network.retrofit.DataToAmoApi
import lk.mzpo.ru.ui.components.RedButton
import lk.mzpo.ru.ui.components.stories.Story
import lk.mzpo.ru.ui.theme.Aggressive_red
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log


class CatalogViewModel
        (
        ): ViewModel()
{
        val courses: MutableState<List<CoursePreview>> = mutableStateOf(emptyList())
        val courses_selected: MutableState<List<CoursePreview>> = mutableStateOf(emptyList())

        val loaded = mutableStateOf(false)
        val categories: MutableState<List<Category>> = mutableStateOf(emptyList())
        val h1 = mutableStateOf("")
        val selected_cat = mutableStateOf(0)

        fun getCourses(context: Context, url: String)
        {
                        val courses_ar = arrayListOf<CoursePreview>()
                        val url1 = "https://lk.mzpo-s.ru/mobile/catalog/$url"
                        val queue = Volley.newRequestQueue(context)
                        val sRequest = StringRequest(
                                Request.Method.GET,
                                url1,
                                {
                                                response ->
                                        val mainobj = JSONArray(response)

                                        for(i in 0 until  mainobj.length())
                                        {
                                                val item = mainobj[i] as JSONObject
                                                val prices = item.getJSONObject("prices")
                                                courses_ar.add(CoursePreview(
                                                        item.getInt("id"),
                                                        item.getString("image"),
                                                        item.getString("name"),
                                                        item.getString("prefix"),
                                                        item.getInt("hours"),
                                                        Prices(
                                                                try {
                                                                        prices.getInt("sale15")
                                                                } catch (e: Exception)
                                                                {
                                                                        null
                                                                },
                                                                try {
                                                                        prices.getInt("ind")
                                                                } catch (e: Exception)
                                                                {
                                                                        null
                                                                },
                                                                try {
                                                                        prices.getInt("weekend")
                                                                } catch (e: Exception)
                                                                {
                                                                        null
                                                                },
                                                                try {
                                                                        prices.getInt("dist")
                                                                } catch (e: Exception)
                                                                {
                                                                        null
                                                                },
                                                        ),
                                                        item.getInt("category"),
                                                        item.getString("doctype")


                                                ))

                                        }
                                        courses.value = courses_ar
                                        courses_selected.value = courses_ar
                                        loaded.value = true

                                },
                                {

                                        Log.d("MyLog", "VolleyError: $it")
                                }
                        )
                        queue.add(sRequest)



                        val cats = arrayListOf<Category>()
                        val url_cats = "https://lk.mzpo-s.ru/mobile/category/$url"
                        val sRequest1 = StringRequest(
                                Request.Method.GET,
                                url_cats,
                                {
                                                response ->

                                        try {
                                                val mainobj = JSONObject(response).getJSONArray("children")
                                                h1.value = JSONObject(response).getString("name")
                                                for(i in 0 until  mainobj.length())
                                                {
                                                        val item = mainobj[i] as JSONObject
                                                        cats.add(
                                                                Category(item.getInt("id"),item.getString("name"), item.getString("alias"), item.getInt("parent_id"), null, item.getString("image"), item.getInt("amount"))
                                                                                                                )


                                                }
                                        } catch (e: Exception)
                                        {
                                                Log.d("MyLog", e.toString())
                                        }


                                        categories.value = cats



                                },
                                {
                                        Log.d("MyLog", "VolleyError: $it")
                                }
                        )

                queue.add(sRequest1)
                }

        fun searchCourses(context: Context, url: String)
        {
                        val courses_ar = arrayListOf<CoursePreview>()
                        val url1 = "https://lk.mzpo-s.ru/mobile/search?q=$url"
                        Log.d("MyLog", url1)

                        val queue = Volley.newRequestQueue(context)
                        val sRequest = StringRequest(
                                Request.Method.GET,
                                url1,
                                {
                                                response ->
                                        Log.d("MyLog", response)
                                        val mainobj = JSONArray(response)
                                        for(i in 0 until  mainobj.length())
                                        {
                                                val item = mainobj[i] as JSONObject
                                                val prices = item.getJSONObject("prices")
                                                courses_ar.add(CoursePreview(
                                                        item.getInt("id"),
                                                        item.getString("image"),
                                                        item.getString("name"),
                                                        item.getString("prefix"),
                                                        item.getInt("hours"),
                                                        Prices(
                                                                try {
                                                                        prices.getInt("sale15")
                                                                } catch (e: Exception)
                                                                {
                                                                        null
                                                                },
                                                                try {
                                                                        prices.getInt("ind")
                                                                } catch (e: Exception)
                                                                {
                                                                        null
                                                                },
                                                                try {
                                                                        prices.getInt("weekend")
                                                                } catch (e: Exception)
                                                                {
                                                                        null
                                                                },
                                                                try {
                                                                        prices.getInt("dist")
                                                                } catch (e: Exception)
                                                                {
                                                                        null
                                                                },
                                                        ),
                                                        item.getInt("category"),
                                                        item.getString("doctype")


                                                ))

                                        }
                                        courses.value = courses_ar
                                        courses_selected.value = courses_ar
                                        loaded.value = true
                                        h1.value = "Вот что мы нашли по запросу \"$url\""

                                },
                                {
                                        Log.d("MyLog", "VolleyError: $it")
                                }
                        )
                        queue.add(sRequest)

                }







        }



