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
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import lk.mzpo.ru.models.Cart
import lk.mzpo.ru.models.Category
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
        val bottomText = mutableStateOf("")
        val Story_lables = listOf(listOf("Акции", R.drawable.group_30),listOf("Мероприятия", R.drawable.group_29),listOf("Подборки", R.drawable.group_32),listOf("Преподаватели", R.drawable.group_31))
        private val _courses: MutableState<List<CoursePreview>> = mutableStateOf(listOf())

        val courses: List<CoursePreview> get() = _courses.value

        val form_title = mutableStateOf("")

        val stories: MutableState<List<List<Story>>> = mutableStateOf(emptyList())
        val step = mutableStateOf(0);

        val banner_sliser = mutableStateOf(listOf<String>())
        val cats_main = mutableStateOf(listOf<Category>())



        val _storyState  = MutableTransitionState(false).apply {
                        targetState = false // start the animation immediately
                }
        val story_position = mutableStateOf(0)
        val paused = mutableStateOf(false)
        val openDialog = MutableTransitionState(false).apply {
                targetState = false // start the animation immediately
        }
        @OptIn(ExperimentalMaterialApi::class)
        val bottomSheetState = mutableStateOf(false)



        @OptIn(ExperimentalMaterialApi::class)
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
                                        val cats = mainobj.getJSONArray("cats")
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
                                                                                if(i == 0 || i == 1)
                                                                                {
                                                                                        Button(
                                                                                                onClick = {
                                                                                                        bottomText.value = story.getString("description")
                                                                                                        paused.value = true
//                                                                                                        openDialog.targetState = true
                                                                                                        bottomSheetState.value = true
                                                                                                        form_title.value = story.getString("name")
                                                                                                },
                                                                                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(android.graphics.Color.parseColor(story.getString("btn_color")))),
                                                                                                shape = RoundedCornerShape(50),
                                                                                                modifier = Modifier.fillMaxWidth(0.7f).padding(bottom = 10.dp)
                                                                                        ) {
                                                                                                Text(text = story.getString("btn_text"), color = Color.White)
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
                                                                        }



                                                                ),
                                                                item.getInt("category"),
                                                                item.getString("doctype")


                                                        )
                                                )
                                        }
                                        _courses.value = previewList
                                        val banners = mainobj.getJSONArray("mainPromoBanner")
                                        val ban_list = arrayListOf<String>()
                                        for (i in 0 until banners.length())
                                        {
                                                ban_list.add(banners[i] as String)
                                        }
                                        banner_sliser.value = ban_list
                                        val cats_list = arrayListOf<Category>()
                                        for (i in 0 until  cats.length())
                                        {
                                              val item = cats.getJSONObject(i)
                                              cats_list.add(Category(item.getInt("id"),item.getString("name"), item.getString("alias"), item.getInt("parent_id"), null, item.getString("image")))

                                        }
                                        cats_main.value = cats_list


                                },
                                {
                                        Log.d("MyLog", "VolleyError: $it")
                                }
                        )
                        queue.add(sRequest)
                }







        }



