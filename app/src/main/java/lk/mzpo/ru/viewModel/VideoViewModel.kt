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
import lk.mzpo.ru.models.Course
import lk.mzpo.ru.models.Document
import lk.mzpo.ru.models.Group
import lk.mzpo.ru.models.Module
import lk.mzpo.ru.models.User
import lk.mzpo.ru.models.UserData
import lk.mzpo.ru.network.retrofit.AuthData
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.log


class VideoViewModel  (
): ViewModel() {


    fun update(
        ctx: Context,
        id: Int,
        time: Float,
        duration: Float,
        spendTime: Float,
        contract: Int
    ) {

        val queue = Volley.newRequestQueue(ctx)
        Log.d("MyLog", time.toString()+" "+duration.toString()+" "+spendTime.toString())
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://lk.mzpo-s.ru/mobile/user/study/saveTime/$id",
            Response.Listener { response ->
                Log.d("MyLog", response)
                Log.d("MyLog", "https://lk.mzpo-s.ru/mobile/user/study/saveTime/$id")
            },

            Response.ErrorListener { error ->
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
                val test = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
                val token = test.getString("token_lk", "")
                headers["Authorization"] = "Bearer " + token?.trim('"')
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                params["time"] = time.toString()
                params["duration"] = duration.toString()
                params["spendTime"] = spendTime.times(1000).toString()
                params["contract"] = contract.toString()
                Log.d("MyLog", params.toString());
                return params
            }
        }

        queue.add(stringRequest)


    }

}