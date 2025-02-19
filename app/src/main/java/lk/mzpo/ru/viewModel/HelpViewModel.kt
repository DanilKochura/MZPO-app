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
import lk.mzpo.ru.BuildConfig
import lk.mzpo.ru.models.User
import lk.mzpo.ru.network.retrofit.Feedback
import org.json.JSONObject


class HelpViewModel  (
): ViewModel()
{

    val user = mutableStateOf(User(0,"","", "", ""))


    /**
     * Отправление формы обратной связи
     * @param feedback - Модель данных обратной связи
     */
    fun sendFeedback(feedback: Feedback, ctx: Context)
    {
        val queue = Volley.newRequestQueue(ctx)

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://trayektoriya.ru/mobile/user/feedback",
            Response.Listener { response ->
                Log.d("FeedbackLog", response)
                try {
                    val json = JSONObject(response)
                    if(json.getString("status") == "success")
                    {
                        Toast.makeText(ctx,"Данные успешно сохранены!", Toast.LENGTH_SHORT).show()
                    }
                } catch (_: Exception)
                {
                    Toast.makeText(ctx, "Произошла ошибка. Попробуйте позже", Toast.LENGTH_SHORT).show()
                }
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
//                headers["Accept"] = "application/json"
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

                params["subject"] = feedback.subject
                params["message"] = feedback.message
//                params["parent"] = feedback.parent.toString()
//                params["type"] = feedback.type.toString()
                params["mobile"] = feedback.mobile.toString()
                params["version"] = BuildConfig.VERSION_NAME
                return params
            }
        }

        queue.add(stringRequest)


    }



}