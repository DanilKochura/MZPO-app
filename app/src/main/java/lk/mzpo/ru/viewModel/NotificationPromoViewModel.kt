package lk.mzpo.ru.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import lk.mzpo.ru.models.study.NotificationNew
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.ui.components.stories.NotificationPromo


class NotificationPromoViewModel  (
): ViewModel()
{


    val auth_tested = mutableStateOf(AuthStatus.TEST)
    val story = mutableStateOf(NotificationPromo())
    val loaded = mutableStateOf(false)

    val notifications = mutableListOf<NotificationNew>();

    fun getData( context: Context, alias: String)
    {
        val url = "https://trayektoriya.ru/mobile/$alias"
        val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val token = test.getString("token_lk", "")
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    Log.d("getData", response)
                    val gson = Gson();
                    story.value = gson.fromJson(response, NotificationPromo::class.java)
                    loaded.value = true
                    Log.d("MyLog", notifications.size.toString())

                },
                Response.ErrorListener { error ->
                    Log.i("mylog", "error = " + error)
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer "+token?.trim('"')
                    return headers
                }

            }
        queue.add(stringReq)
    }








}