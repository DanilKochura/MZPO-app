package lk.mzpo.ru.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import lk.mzpo.ru.models.Contract
import lk.mzpo.ru.models.User
import lk.mzpo.ru.models.study.Notification
import lk.mzpo.ru.models.study.NotificationNew
import lk.mzpo.ru.network.retrofit.AuthData
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import org.json.JSONArray
import org.json.JSONObject


class NotificationsViewModel  (
): ViewModel()
{


    val auth_tested = mutableStateOf(AuthStatus.TEST)

    val loaded = mutableStateOf(false)

    val notifications = mutableListOf<NotificationNew>();

    fun getData(token: String, context: Context)
    {
        val url = "https://lk.mzpo-s.ru/mobile/user/notifications"
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    Log.d("getData", response)
                    val gson = Gson();
                    val json = JSONArray(response)
                    for (i in 0 until  json.length())
                    {
                        val string = json[i].toString()
                        notifications.add(gson.fromJson(string, NotificationNew::class.java))
                    }
                    loaded.value = true
                    Log.d("MyLog", notifications.size.toString())

                },
                Response.ErrorListener { error ->
                    loaded.value = true
                    Log.i("mylog", "error = " + error)
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer "+token.trim('"')
                    return headers
                }

            }
        queue.add(stringReq)
    }








}