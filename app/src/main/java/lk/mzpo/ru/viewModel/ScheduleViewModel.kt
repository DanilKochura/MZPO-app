package lk.mzpo.ru.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import lk.mzpo.ru.models.User
import lk.mzpo.ru.models.study.UserSchedule
import org.json.JSONArray
import org.json.JSONObject


class ScheduleViewModel  (
): ViewModel()
{
    val schedule = mutableListOf<UserSchedule>()
    val user = mutableStateOf(User(0,"","", "",""))

    fun getData(context: Context)
    {
        val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val token = test.getString("token_lk", "")
        val url = "https://trayektoriya.ru/mobile/user/schedule"
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->

                     getContracts(response)

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

    private fun getContracts(response: String?) {
        val mainObject = JSONArray(response)
        val gson = Gson()
        schedule.clear()
        for(i in 0 until mainObject.length())
        {
            schedule.add(gson.fromJson(mainObject[i].toString(), UserSchedule::class.java))
        }
    }

    fun getUser(response: String): User {
        if (response.isEmpty()) return User(0,"","", "","")
        val mainObject = JSONObject(response)
        return User(
            mainObject.getInt("id"),
            mainObject.getString("name"),
            mainObject.getString("email"),
            mainObject.getString("phone"),
            mainObject.getString("id_1c"),
            mainObject.getString("avatar"),
            mainObject.getInt("job_access"),
        )
    }

}