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
import com.google.gson.JsonObject
import lk.mzpo.ru.models.Course
import lk.mzpo.ru.models.Document
import lk.mzpo.ru.models.Group
import lk.mzpo.ru.models.Module
import lk.mzpo.ru.models.User
import lk.mzpo.ru.models.UserData
import lk.mzpo.ru.models.study.ActiveMaterials
import lk.mzpo.ru.models.study.Question
import lk.mzpo.ru.models.study.TestBase
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


class TestViewModel  (
): ViewModel()
{

    val loaded = mutableStateOf(false)

    val questionCount = mutableStateOf(0)

    val final = mutableStateOf(false)

    val attemptsLeft = mutableStateOf(0)

    val testStarted = mutableStateOf(false)

    val hasNotFinishedAttempt = mutableStateOf(true)

    val question = mutableStateOf<Question?>(null)
    val material = mutableStateOf<ActiveMaterials?>(null)

    fun getData(context: Context, contractId: Int, testId: Int)
    {
        val url = "https://lk.mzpo-s.ru/mobile/user/test/$contractId/$testId"
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    parseData(response)
                    loaded.value = true

                },
                Response.ErrorListener { error ->
                    Log.i("mylog", "error = " + error)
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                    val token = test.getString("token_lk", "")
                    headers["Authorization"] = "Bearer "+token?.trim('"')
                    return headers
                }

            }
        queue.add(stringReq)
    }

    fun parseData(response: String)
    {
        Log.d("MyLog", response)
//        val mainObj = JsonObject(response)
        val gson = Gson()
        val test  = gson.fromJson(response, TestBase::class.java) as TestBase
        this.attemptsLeft.value = test.attemptsLeft!!
        this.final.value = test.final!!
        this.hasNotFinishedAttempt.value = test.hasNotFinishedAttempt!!
        this.question.value = test.question
        this.questionCount.value = test.questionsCount!!
        this.material.value = test.material!!

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

    fun sendSingle(ctx: Context, contractId: Int, materialId: Int, answer: Int, question: Int)
    {
        val queue = Volley.newRequestQueue(ctx)

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://lk.mzpo-s.ru/mobile/user/test/$contractId/$materialId/question/$question",
            Response.Listener { response ->
                val mainobj = JSONObject(response)
                val quest = mainobj.getJSONObject("question")
                val gson = Gson();
                this.question.value = gson.fromJson(quest.toString(), Question::class.java)
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
                params["answer"] = answer.toString()
                return params
            }
        }

        queue.add(stringRequest)


    }



}