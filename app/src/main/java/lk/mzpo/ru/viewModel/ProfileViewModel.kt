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
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.log


class ProfileViewModel  (
): ViewModel()
{
    val courses = mutableStateListOf<CoursePreview>()

    val auth_tested = mutableStateOf(AuthStatus.TEST)

    val user = mutableStateOf(User(0,"","", "", ""))

    val loaded = mutableStateOf(false)
    val error = mutableStateOf(false)
    fun getData(token: String, context: Context)
    {
        val url = "https://lk.mzpo-s.ru/mobile/user/test"
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    user.value = getUser(response)
                    loaded.value = true

                },
                Response.ErrorListener { err ->
                    loaded.value = true
                    if(err?.networkResponse !== null)
                    {
                        if(err.networkResponse.statusCode == 500)
                        {
                            error.value = true
                        }
                    }
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

    fun getUser(response: String): User {
        Log.d("ProfileLog", response)
        if (response.isEmpty()) return User(0,"","", "", "")
        val mainObject = JSONObject(response)
//        val data = mainObject.getJSONObject("auth_user_data")
//        val gson = Gson()
//        var mUser = gson.fromJson(data.toString(), UserData::class.java)
        return User(
            mainObject.getInt("id"),
            mainObject.getString("name"),
            mainObject.getString("email"),
            mainObject.getString("phone"),
            mainObject.getString("id_1c"),
            mainObject.getString("avatar")
        )

    }


    fun update(phone: String, email: String, name: String, ctx: Context)
    {
        val queue = Volley.newRequestQueue(ctx)

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://lk.mzpo-s.ru/mobile/user/profile",
            Response.Listener { response ->
               Toast.makeText(ctx,"Данные успешно сохранены!", Toast.LENGTH_SHORT).show()
            },

            Response.ErrorListener { error ->
                Toast.makeText(ctx, "Произошла ошибка. Попробуйте позже", Toast.LENGTH_SHORT).show()
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
                headers["Authorization"] = "Bearer "+token?.trim('"')
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                params["name"] = name
                params["email"] = email
                params["phone"] = phone
                return params
            }
        }

        queue.add(stringRequest)


    }


    fun userData(userData: UserData, ctx: Context)
    {
        val queue = Volley.newRequestQueue(ctx)

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://lk.mzpo-s.ru/mobile/user/userData",
            Response.Listener { response ->
                Log.d("userData", response)
                Toast.makeText(ctx,"Данные успешно сохранены!", Toast.LENGTH_SHORT).show()
            },

            Response.ErrorListener { error ->
                Toast.makeText(ctx, "Произошла ошибка. Попробуйте позже", Toast.LENGTH_SHORT).show()
                Log.d("userData", error.localizedMessage?.toString() ?: "")
                error.printStackTrace()
            }) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded"
            }

            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> =
                    HashMap()
//                headers["Content-Type"] = "application/x-www-form-urlencoded"
                val test = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
                val token = test.getString("token_lk", "")
                Log.d("userData", "Bearer "+token?.trim('"'))
                headers["Authorization"] = "Bearer "+token?.trim('"')
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                params["avatar"] = userData.avatar.toString()
                params["address"] = userData.address.toString()
                params["birthday"] = userData.birthday.toString()
                params["sex"] = userData.sex.toString()
                params["snils"] = userData.snils.toString()
                params["pass_series"] = userData.passSeries.toString()
                params["pass_number"] = userData.passNumber.toString()
                params["pass_doi"] = userData.passDoi.toString()
                params["pass_poi"] = userData.passPoi.toString()
                params["pass_dpt"] = userData.passDpt.toString()
                params["pass_registration"] = userData.passRegistration.toString()
                return params
            }
        }

        queue.add(stringRequest)


    }



}