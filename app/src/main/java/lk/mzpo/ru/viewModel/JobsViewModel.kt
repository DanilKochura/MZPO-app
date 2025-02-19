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
import lk.mzpo.ru.models.User
import org.json.JSONObject


class JobsViewModel  (
): ViewModel()
{
    val user = mutableStateOf(User(0,"","", "", ""))

    val agreement = mutableStateOf(false)

    val auth = mutableStateOf(false)

    /**
     * Регистрация пользователя через api jobs.mzpo-s.ru
     * @param ctx - Контекст для очереди
     */
    fun register(ctx: Context)
    {
        val queue = Volley.newRequestQueue(ctx)

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://trayektoriya.ru/mobile/user/jobs",
            Response.Listener { response ->
            try {
                Log.d("jobsRequest", response)
                val json = JSONObject(response)
                if(json.getString("status") == "success")
                {
                    Toast.makeText(ctx,"Данные успешно сохранены!", Toast.LENGTH_SHORT).show()
                    auth.value = true
                } else
                {
                    Toast.makeText(ctx,"Произошла ошибка. Попробуйте позже.", Toast.LENGTH_SHORT).show()

                }
            } catch (e: Exception)
            {
                Log.d("jobsRequest", e.toString())
                Toast.makeText(ctx,"Произошла ошибка. Попробуйте позже.", Toast.LENGTH_SHORT).show()

            }
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
                return params
            }
        }

        queue.add(stringRequest)


    }



}