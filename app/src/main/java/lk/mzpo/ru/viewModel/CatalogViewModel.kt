package lk.mzpo.ru.viewModel

import CoursePreview
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import lk.mzpo.ru.models.Category
import org.json.JSONArray
import org.json.JSONObject


class CatalogViewModel
    (
) : ViewModel() {

    val courses = mutableListOf<CoursePreview>() // общий массив полученных курсов
    val courses_selected: MutableState<List<CoursePreview>> =
        mutableStateOf(emptyList()) // выбранные курсы при фильтрации на страницах факультетов

    val maxPageExpired: MutableState<Boolean> =
        mutableStateOf(false) // флаг достижения последней страницы

    val loaded = mutableStateOf(false) // флаг успешной загрузки контента
    val categories: MutableState<List<Category>> =
        mutableStateOf(emptyList()) // массив категорий для страниц факультетов
    val h1 = mutableStateOf("") //заголовок категории
    val selected_cat = mutableStateOf(0) // выбранная категория для страниц факультетов

    val page = mutableStateOf(0) // номер следующей страницы для подгрузки


    /**
     * Получение списка курсов для выбранной категории
     * @param context - Контекст для очереди
     * @param url - Алиас выбранной категории
     */
    fun getCourses(context: Context, url: String) {
        //region Получение курсов
        Log.d("MyCataLog", "started "+page.value.toString())

        val url1 = "https://trayektoriya.ru/mobile/catalog/$url/${page.value}"
        val queue = Volley.newRequestQueue(context)
        val sRequest = StringRequest(
            Request.Method.GET,
            url1,
            { response ->
                val mainobj = JSONArray(response)

                for (i in 0 until mainobj.length()) {
                    try {
                        val item = mainobj[i] as JSONObject
                        val gson = Gson()
                        val course = gson.fromJson(item.toString(), CoursePreview::class.java)
                        courses.add(course)
                    } catch (_: Exception) {
                    }

                }
                courses_selected.value = courses // по умолчанию выбраны все курсы
                page.value++
                loaded.value = true

                Log.d("MyCataLog", courses_selected.value.size.toString())
                if (courses.size < 20)
                {
                    maxPageExpired.value = true

                }
            },
            {
                maxPageExpired.value = true
                Log.d("MyCataLog", "VolleyError: $it")
            }
        )
        queue.add(sRequest)
        //endregion


        //region Получение вложенных категорий категорий
        val cats = arrayListOf<Category>()
        val url_cats = "https://trayektoriya.ru/mobile/category/$url"
        if(url.isNotBlank() and url.isNotEmpty())
        {
            val sRequest1 = StringRequest(
                Request.Method.GET,
                url_cats,
                { response ->

                    try {
                        val mainobj = JSONObject(response).getJSONArray("children")
                        h1.value = JSONObject(response).getString("name")
                        for (i in 0 until mainobj.length()) {
                            val item = mainobj[i] as JSONObject
                            cats.add(
                                Category(
                                    item.getInt("id"),
                                    item.getString("name"),
                                    item.getString("alias"),
                                    item.getInt("parent_id"),
                                    null,
                                    item.getString("image"),
                                    item.getInt("amount")
                                )
                            )


                        }
                    } catch (e: Exception) {
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

        //endregion
    }

    /**
     * Получение курсов по запросу из строки поиска
     * @param context - Контекст для очереди
     * @param query - Строка запроса (минимум 3 символа)
     */
    fun searchCourses(context: Context, query: String) {
        val url1 = "https://trayektoriya.ru/mobile/search?q=$query"

        if (maxPageExpired.value) { // не делать повторный запрос, если ЛК вернул ошибку
            return
        }
        val queue = Volley.newRequestQueue(context)
        val sRequest = StringRequest(
            Request.Method.GET,
            url1,
            { response ->
                Log.d("MyLog", response)
                val mainobj = JSONArray(response)
                for (i in 0 until mainobj.length()) {
                    val item = mainobj[i] as JSONObject
                    val gson = Gson()
                    val course = gson.fromJson(item.toString(), CoursePreview::class.java)
                    courses.add(course)
                }

                courses_selected.value = courses
                loaded.value = true
                this.maxPageExpired.value = true
                h1.value = "Вот что мы нашли по запросу \"$query\""

            },
            {
                Log.d("MyLog", "VolleyError: $it")
                maxPageExpired.value = true
            }
        )
        queue.add(sRequest)

    }


}



