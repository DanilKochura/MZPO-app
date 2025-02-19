package lk.mzpo.ru.viewModel

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


class CategoriesViewModel
        (
        ): ViewModel()
{

        val categories: MutableState<List<Category>> = mutableStateOf(emptyList()) // массив категорий (вложенный)


        /**
         * Получение списка категорий
         * @param context - Контекст для очереди
         */
        fun getCategories(context: Context)
        {
                        val cats = arrayListOf<Category>()
                        val url = "https://trayektoriya.ru/mobile/categories"
                        val queue = Volley.newRequestQueue(context)
                        val sRequest = StringRequest(
                                Request.Method.GET,
                                url,
                                {
                                                response ->

                                        val mainobj = JSONArray(response)

                                        for(i in 0 until  mainobj.length())
                                        {
                                                val item = mainobj[i] as JSONObject
                                                val gson = Gson()
                                                cats.add(gson.fromJson(item.toString(), Category::class.java))

                                        }
                                        categories.value = cats
                                },
                                {
                                        Log.d("MyLog", "VolleyError: $it")
                                }
                        )
                        queue.add(sRequest)
                }







        }



