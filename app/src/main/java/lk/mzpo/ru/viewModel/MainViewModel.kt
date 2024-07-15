package lk.mzpo.ru.viewModel

import CoursePreview
import Prices
import android.R.attr.password
import android.R.attr.targetActivity
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
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lk.mzpo.ru.BuildConfig
import lk.mzpo.ru.R
import lk.mzpo.ru.models.Cart
import lk.mzpo.ru.models.Category
import lk.mzpo.ru.models.Course
import lk.mzpo.ru.network.retrofit.AuthStatus
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

data class BannerItems(
    @SerializedName("image") val image: String,
    @SerializedName("link") val link: String? = null,
)
class MainViewModel
    (
) : ViewModel() {
    val auth_tested = mutableStateOf(AuthStatus.TEST) //флаг аунтификации
    val bottomText = mutableStateOf("") // динамический текст для модального окна
    val event_name = mutableStateOf("") // динамический текст для модального окна
    val event = mutableStateOf(false) // динамический текст для модального окна
    val Story_lables = listOf(
        listOf("Акции", R.drawable.group_30),
        listOf("Мероприятия", R.drawable.group_29),
        listOf("Подборки", R.drawable.group_32),
        listOf("Преподаватели", R.drawable.group_31)
    ) //икноки для сториз
    private val _courses: MutableState<List<CoursePreview>> = mutableStateOf(listOf())
    val analytics: FirebaseAnalytics = Firebase.analytics
    val courses: List<CoursePreview> get() = _courses.value

    val form_title = mutableStateOf("")
    val form_name_site = mutableStateOf("")

    val stories: MutableState<List<List<Story>>> = mutableStateOf(emptyList()) // список историй
    val step = mutableStateOf(0);


    val banner_sliser = mutableStateOf(listOf<BannerItems>()) //список ссылок на баннеры для слайдера
    val cats_main = mutableStateOf(listOf<Category>()) //категории для блока с направлениями


    //region Вспомогательные переменные для историй
    val _storyState = MutableTransitionState(false).apply {
        targetState = false // start the animation immediately
    }
    val story_position = mutableStateOf(0)
    val paused = mutableStateOf(false)
    //endregion
    val pipeline = mutableStateOf(3198184)
    val openDialog = MutableTransitionState(false).apply {
        targetState = false // start the animation immediately
    }

    val app_version = mutableStateOf(BuildConfig.VERSION_NAME)

    @OptIn(ExperimentalMaterialApi::class)
    val bottomSheetState = mutableStateOf(false)

    lateinit var navHostController: NavHostController

    /**
     * Получение всей информации для главной страницы
     */
    @OptIn(ExperimentalMaterialApi::class)
    fun getStories(context: Context, uri: UriHandler) {
        val url = "https://lk.mzpo-s.ru/mobile/main"
        val queue = Volley.newRequestQueue(context)
        val sRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->

                val mainobj = JSONObject(response)
                val events = mainobj.getJSONArray("events")
                val promos = mainobj.getJSONArray("promos")
                app_version.value = mainobj.getString("version")
                    val gson = Gson()
                val cats = mainobj.getJSONArray("cats")
                val popular = mainobj.getJSONArray("popular")
                val teachers = mainobj.getJSONArray("teachers")
                val dictionary = listOf("promos", "events", "recomended", "teachers")
                val list = arrayListOf<List<Story>>()
                val api = listOf(promos, events, events, teachers)

                //region Stories
                for (i in dictionary.indices) {
                    val item = mainobj.getJSONArray(dictionary[i])
                    val cat = arrayListOf<Story>()
                    for (j in 0 until item.length()) {

                        val story = item[j] as JSONObject
                        try {
                            cat.add(
                                Story(
                                    story.getString("image")
                                ) {
                                    if ((i == 0 || i == 1 || i == 3)) {
                                        var btn_text = "Подробнее"
                                        var btn_color = Aggressive_red
                                        try {
                                            btn_color = Color(
                                                android.graphics.Color.parseColor(
                                                    story.getString("btn_color")
                                                )
                                            )
                                        } catch (_: Exception) {
                                        }
                                        try {
                                            btn_text = story.getString("btn_text");
                                        } catch (_: Exception) {
                                        }

                                        var is_event = false
                                        var is_event_name = ""
                                        var link = "";
                                        var additional = "";

                                        if (i == 1)
                                        {
                                            is_event = true
                                            is_event_name = story.getString("name")
//                                            additional = " мероприятие "
                                        } else if (i ==0)
                                        {
//                                            additional = " акцию "
                                        }

                                        try {
                                            link = story.getString("link")
                                        }catch (_: Exception){}
                                        Button(
                                            onClick = {
                                                if (link.isNotEmpty())
                                                {
                                                    uri.openUri(link)
                                                    return@Button
                                                }
                                                if (story.getInt("id") == 76 && auth_tested.value !== AuthStatus.AUTH)
                                                {
                                                    navHostController.navigate("register")
                                                } else
                                                {
                                                    bottomText.value = story.getString("description")
                                                    paused.value = true
                                                    if (is_event)
                                                    {
                                                        pipeline.value = story.getInt("pipeline")
                                                    }
                                                    Log.d("MyLogPIP", pipeline.value.toString())
                                                    event.value = is_event
                                                    event_name.value = is_event_name
//
//                                                                                                                openDialog.targetState = true
                                                    bottomSheetState.value = true
                                                    form_title.value = story.getString("name")
                                                    form_name_site.value = additional+story.getString("name")
                                                }

                                            },
                                            colors = ButtonDefaults.buttonColors(backgroundColor = btn_color),
                                            shape = RoundedCornerShape(50),
                                            modifier = Modifier
                                                .fillMaxWidth(
                                                    0.7f
                                                )
                                                .padding(
                                                    bottom = 10.dp
                                                )
                                        ) {
                                            Log.d("MyLogStory", "RENDERED")
                                            Text(text = btn_text, color = Color.White)
                                        }
                                    } else if (i == 2) {
                                        Button(
                                            onClick = {
                                                navHostController.navigate(
                                                    "course/" + story.getString(
                                                        "course_alias"
                                                    )
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red),
                                            shape = RoundedCornerShape(50),
                                            modifier = Modifier
                                                .fillMaxWidth(
                                                    0.7f
                                                )
                                                .padding(
                                                    bottom = 10.dp
                                                )
                                        ) {
                                            Text(text = "Записаться", color = Color.White)
                                        }

                                    }

                                }
                            )
                        } catch (e: Exception) {
                            Log.d("MyLog1", e.toString())
                        }
                    }
                    list.add(cat)
                }
                stories.value = list
                //endregion

                //region Courses
                val previewList = arrayListOf<CoursePreview>()
                for (i in 0 until popular.length()) {
                    val item = popular[i] as JSONObject
                    val prices = item.getJSONObject("prices")

                    previewList.add(
                        gson.fromJson(item.toString(), CoursePreview::class.java)
                    )
                }
                _courses.value = previewList
                //endregion

                //region Banners
                val banners = mainobj.getJSONArray("mainPromoBanner")
                val ban_list = arrayListOf<BannerItems>()
                for (i in 0 until banners.length()) {
                    ban_list.add(gson.fromJson(banners[i].toString(), BannerItems::class.java))
                }
                banner_sliser.value = ban_list
                //endregion

                //region Banners
                val cats_list = arrayListOf<Category>()
                for (i in 0 until cats.length()) {
                    val item = cats.getJSONObject(i)
                    cats_list.add(
                        gson.fromJson(item.toString(), Category::class.java)
                    )

                }
                cats_main.value = cats_list
                //endregion


            },
            {
                Log.d("MyLog", "VolleyError: $it")
            }
        )
        queue.add(sRequest)
    }


}



