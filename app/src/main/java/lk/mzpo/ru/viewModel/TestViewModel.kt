package lk.mzpo.ru.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import lk.mzpo.ru.models.study.ActiveMaterials
import lk.mzpo.ru.models.study.Question
import lk.mzpo.ru.models.study.TestAttempt
import lk.mzpo.ru.models.study.TestBase
import lk.mzpo.ru.models.study.TestResult
import org.json.JSONObject


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
    val result = mutableStateOf<TestResult?>(null)
    val finished = mutableStateOf(false)
    val material = mutableStateOf<ActiveMaterials?>(null)
    val attempts = mutableStateOf(
        arrayListOf<TestAttempt>())

    fun getData(context: Context, contractId: Int, testId: Int)
    {
        val url = "https://trayektoriya.ru/mobile/user/test/$contractId/$testId"
        Log.d("MyTestLog", url)

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
        Log.d("MyTestLog", question.value?.activeAnswers?.size.toString())
        this.questionCount.value = test.questionsCount!!
        this.material.value = test.material!!
        this.attempts.value = test.material!!.attempts
    }




    fun update(phone: String, email: String, name: String, ctx: Context)
    {
        val queue = Volley.newRequestQueue(ctx)

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://trayektoriya.ru/mobile/user/profile",
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
            "https://trayektoriya.ru/mobile/user/test/$contractId/$materialId/question/$question",
            Response.Listener { response ->
                val mainobj = JSONObject(response)
                val gson = Gson();
                try {
                   val quest = mainobj.getJSONObject("question")
                   this.question.value = gson.fromJson(quest.toString(), Question::class.java)
               } catch (_: Exception)
               {
                   Log.d("MyTestLog", "TEST")
                   val quest = mainobj.getJSONObject("result")
                   this.result.value = gson.fromJson(quest.toString(), TestResult::class.java)
                   Log.d("MyTestLog", result.value.toString())
                   finished.value = true
               }
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

    fun sendMultiple(ctx: Context, contractId: Int, materialId: Int, answer: MutableList<Int>, question: Int)
    {
        val queue = Volley.newRequestQueue(ctx)
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://trayektoriya.ru/mobile/user/test/$contractId/$materialId/question/$question",
            Response.Listener { response ->
                val mainobj = JSONObject(response)
                val gson = Gson();
                try {
                    val quest = mainobj.getJSONObject("question")
                    this.question.value = gson.fromJson(quest.toString(), Question::class.java)
                } catch (_: Exception)
                {
                    Log.d("MyTestLog", "TEST")
                    val quest = mainobj.getJSONObject("result")
                    this.result.value = gson.fromJson(quest.toString(), TestResult::class.java)
                    Log.d("MyTestLog", result.value.toString())
                    finished.value = true
                }
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
                params["answers"] = answer.joinToString(" ")
                Log.d("MyLog", params.toString())
                return params
            }
        }

        queue.add(stringRequest)


    }



}