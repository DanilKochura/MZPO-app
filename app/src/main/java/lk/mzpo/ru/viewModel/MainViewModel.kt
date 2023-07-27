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
import androidx.compose.material.Text
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
import lk.mzpo.ru.models.Course
import lk.mzpo.ru.network.retrofit.Data2Amo
import lk.mzpo.ru.network.retrofit.DataToAmoApi
import lk.mzpo.ru.ui.components.RedButton
import lk.mzpo.ru.ui.components.stories.Story
import lk.mzpo.ru.ui.theme.Aggressive_red
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log


class MainViewModel
        (
        ): ViewModel()
{
        val Story_lables = listOf(listOf("Акции", R.drawable.group_30),listOf("Мероприятия", R.drawable.group_29),listOf("Подборки", R.drawable.group_32),listOf("Преподаватели", R.drawable.group_31))
        private val _courses: MutableState<List<CoursePreview>> = mutableStateOf(listOf())

        val courses: List<CoursePreview> get() = _courses.value

        val form_title = mutableStateOf("")

        val stories: MutableState<List<List<Story>>> = mutableStateOf(emptyList())
        val step = mutableStateOf(0);

        val _storyState  = MutableTransitionState(false).apply {
                        targetState = false // start the animation immediately
                }
        val story_position = mutableStateOf(0)
        val paused = mutableStateOf(false)
        val openDialog = MutableTransitionState(false).apply {
                targetState = false // start the animation immediately
        }


        fun sendDataToAmo(data: Data2Amo, ctx: Context)
        {
                val queue = Volley.newRequestQueue(ctx)

                val stringRequest: StringRequest = object : StringRequest(
                        Method.POST,
                        "https://mzpo2amo.ru/siteform/retail",
                        Response.Listener { response ->
                                Log.d("MyLog", response.toString())
                                Toast.makeText(ctx, "Заявка оставлена!", Toast.LENGTH_SHORT).show()
                        },

                        Response.ErrorListener { error ->
                                Toast.makeText(ctx, "Произошла ошибка. Попробуйте позже", Toast.LENGTH_SHORT).show()
                                error.printStackTrace()
                        }) {
                        override fun getBodyContentType(): String {
                                return "application/x-www-form-urlencoded"
                        }

                        override fun getHeaders(): Map<String, String> {
                                val headers: MutableMap<String, String> =
                                        HashMap()
                                headers["Content-Type"] = "application/x-www-form-urlencoded"
                                headers["Accept"] = "application/json"
                                return headers
                        }

                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String> {
                                val params: MutableMap<String, String> =
                                        HashMap()
                                params["name"] = data.name
                                params["form_name_site"] = data.form_name_site
                                params["email"] = data.email
                                params["phone"] = data.phone
                                if(data.comment != "")
                                {
                                        params["comment"] = data.comment
                                } else
                                {
                                        params["comment"] = data.form_name_site
                                }
                                Log.d("MyLog", params["name"].toString())
                                return params
                        }
                }

                queue.add(stringRequest)


        }

        fun getStories(context: Context)
        {
                        val url = "https://lk.mzpo-s.ru/mobile/main"
                        val queue = Volley.newRequestQueue(context)
                        val sRequest = StringRequest(
                                Request.Method.GET,
                                url,
                                {
                                                response ->

                                        val mainobj = JSONObject(response)
                                        val events = mainobj.getJSONArray("events")
                                        val promos = mainobj.getJSONArray("promos")
                                        val popular = mainobj.getJSONArray("popular")
                                        val teachers = mainobj.getJSONArray("teachers")
                                        val dictionary = listOf("promos", "events", "popular", "teachers")
                                        val list = arrayListOf<List<Story>>()
                                        val api = listOf(promos, events, events, teachers)
                                        for(i in dictionary.indices)
                                        {
                                                val item = mainobj.getJSONArray(dictionary[i])
                                                val cat = arrayListOf<Story>()
                                                for (j in 0 until item.length())
                                                {

                                                        val story = item[j] as JSONObject
                                                        Log.d("MyLog", story.toString())
                                                        try {
                                                                cat.add(
                                                                        Story(
                                                                                story.getString("image")
                                                                        ) {
                                                                                if(i == 0 || i == 3)
                                                                                {
                                                                                        Button(
                                                                                                onClick = {
                                                                                                        paused.value = true
                                                                                                        openDialog.targetState = true
                                                                                                        form_title.value = story.getString("name")
                                                                                                },
                                                                                                colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red),
                                                                                                shape = RoundedCornerShape(50),
                                                                                                modifier = Modifier.fillMaxWidth(0.7f).padding(bottom = 10.dp)
                                                                                        ) {
                                                                                                Text(text = "Оставить заявку", color = Color.White)
                                                                                        }
                                                                                }

                                                                        }
                                                                )
                                                        } catch (e: Exception)
                                                        {
                                                                Log.d("MyLog1", e.toString())
                                                        }
                                                }
                                                list.add(cat)
                                        }
                                        stories.value = list
                                        val previewList = arrayListOf<CoursePreview>()
                                        for (i in 0 until popular.length())
                                        {
                                                val item = popular[i] as JSONObject
                                                val prices = item.getJSONObject("prices")

                                                previewList.add(
                                                        CoursePreview(
                                                                item.getInt("id"),
                                                                item.getString("image"),
                                                                item.getString("name"),
                                                                item.getString("prefix"),
                                                                item.getInt("hours"),
                                                                Prices(
                                                                        prices.getInt("sale15"),
                                                                        prices.getInt("weekend"),
                                                                        prices.getInt("ind")
                                                                )

                                                        )
                                                )
                                        }
                                        _courses.value = previewList



                                },
                                {
                                        Log.d("MyLog", "VolleyError: $it")
                                }
                        )
                        queue.add(sRequest)
                }







        }



