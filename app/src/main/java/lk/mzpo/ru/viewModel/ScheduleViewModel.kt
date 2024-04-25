package lk.mzpo.ru.viewModel

import CoursePreview
import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import lk.mzpo.ru.models.Contract
import lk.mzpo.ru.models.Course
import lk.mzpo.ru.models.Document
import lk.mzpo.ru.models.Group
import lk.mzpo.ru.models.Module
import lk.mzpo.ru.models.User
import lk.mzpo.ru.models.UserData
import lk.mzpo.ru.models.study.UserSchedule
import lk.mzpo.ru.network.retrofit.AuthData
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class ScheduleViewModel  (
): ViewModel()
{
    val schedule = mutableListOf<UserSchedule>()
    val user = mutableStateOf(User(0,"","", "",""))

    fun getData(context: Context)
    {
        val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val token = test.getString("token_lk", "")
        val url = "https://lk.mzpo-s.ru/mobile/user/schedule"
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