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


class CategoriesViewModel
        (
        ): ViewModel()
{

        val stories: MutableState<List<Category>> = mutableStateOf(emptyList())





        fun getStories(context: Context)
        {
                        val cats = arrayListOf<Category>()
                        val url = "https://lk.mzpo-s.ru/mobile/categories"
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
                                                val children = item.getJSONArray("children")
                                                val child = arrayListOf<Category>()
                                                for(j in  0 until children.length())
                                                {
                                                        val it = children[j] as JSONObject
                                                        child.add(Category(it.getInt("id"),it.getString("name"), it.getString("alias"), it.getInt("parent_id"), null, it.getString("image"),it.getInt("amount")))
                                                }
                                                cats.add(Category(item.getInt("id"),item.getString("name"), item.getString("alias"), item.getInt("parent_id"), child))

                                        }
                                        stories.value = cats
                                },
                                {
                                        Log.d("MyLog", "VolleyError: $it")
                                }
                        )
                        queue.add(sRequest)
                }







        }



