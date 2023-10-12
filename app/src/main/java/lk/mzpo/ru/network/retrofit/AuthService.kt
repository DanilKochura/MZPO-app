package lk.mzpo.ru.network.retrofit

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray

class AuthService {

    companion object
    {
        fun login(data: AuthData, ctx: Context, navHostController: NavHostController, redirect: Boolean = true)
        {
            val queue = Volley.newRequestQueue(ctx)

            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                "https://lk.mzpo-s.ru/mobile/login",
                Response.Listener { response ->
                    val test =  ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
                    test.edit().putString("token_lk", response).apply()
                    Log.d("MyLog", "AUTHORISED")
                    navHostController.navigate("contracts")
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
                    return headers
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> =
                        HashMap()
                    params["email"] = data.email
                    params["password"] = data.passrowd
                    params["device_name"] = data.device_name
                    return params
                }
            }

            queue.add(stringRequest)


        }

        /**
         * Метод проверки авторизации -
         * отправляет запрос с токеном: в ответ либо guest либо auth
         */
        fun testAuth(context: Context, navHostController: NavHostController, auth_tested: MutableState<Boolean>, redirect: Boolean = true){
            val url = "https://lk.mzpo-s.ru/api/testAuth"
            val queue = Volley.newRequestQueue(context)
            val stringReq: StringRequest =
                object : StringRequest(
                    Method.GET, url,
                    Response.Listener { response ->
                        Log.d("AuthTest", response)
                        val resp = JSONArray(response)
                        if(resp.getString(0).equals("guest"))
                        {
                            Log.d("AuthTest", "guested")

                            val test =  context.getSharedPreferences("session", Context.MODE_PRIVATE)
                            val gson = Gson()
                            val authData = test.getString("auth_data", "")
                            if(authData.isNullOrEmpty())
                            {
                                if(redirect)
                                {
                                    navHostController.navigate("login")
                                }
                            } else
                            {
                                val data: AuthData = gson.fromJson(authData, AuthData::class.java);
                                this.login(data, context, navHostController, redirect)
                                auth_tested.value = true

                            }

                        } else
                        {
                            auth_tested.value = true
                        }


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
    }
}