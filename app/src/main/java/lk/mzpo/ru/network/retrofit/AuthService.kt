package lk.mzpo.ru.network.retrofit

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class AuthService {

    companion object
    {
        fun login(data: AuthData, ctx: Context, navHostController: NavHostController, redirect: Boolean = true, customRedirect: () -> Unit = {})
        {
            val queue = Volley.newRequestQueue(ctx)

            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                "https://trayektoriya.ru/mobile/login",
                Response.Listener { response ->
                    val test =  ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
                    test.edit().putString("token_lk", response).apply()
                    Log.d("MyLog", "AUTHORISED")
                    if (redirect)
                    {
                        navHostController.navigate("contracts")
                    }
                    customRedirect.invoke()
                },

                Response.ErrorListener { error ->
                    val test = ctx.getSharedPreferences(
                        "session",
                        Context.MODE_PRIVATE
                    )

                    test.edit().remove("token_lk").apply()
                    test.edit().remove("auth_data").apply()
                    if (error.networkResponse.statusCode == 401)
                    {
                        val responseBody = String(error.networkResponse.data)
                        try {
                            val errorJson = JSONObject(responseBody)
                            val errorMessage = errorJson.optString("error", "Произошла ошибка. Попробуйте позже")
                            Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(ctx, "Произошла ошибка. Попробуйте позже", Toast.LENGTH_SHORT).show()
                        }
                    } else
                    {
                        Toast.makeText(ctx, "Произошла ошибка. Попробуйте позже", Toast.LENGTH_SHORT).show()
                    }
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
                    Log.d("MyLog", data.email+" "+data.passrowd+" "+data.device_name)
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
        fun testAuth(context: Context, navHostController: NavHostController, auth_tested: MutableState<AuthStatus>, redirect: Boolean = true){
            val url = "https://trayektoriya.ru/api/testAuth"
            val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
            val token = test.getString("token_lk", "")
            if(token.isNullOrBlank())
            {
                auth_tested.value = AuthStatus.GUEST
                return
            }

            if (auth_tested.value != AuthStatus.TEST)
            {
                return
            }


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
                            test
                                .edit()
                                .remove("token_lk")
                                .apply()
                            test
                                .edit()
                                .remove("auth_data")
                                .apply()
                            val gson = Gson()
                            auth_tested.value = AuthStatus.GUEST
                            val authData = test.getString("auth_data", "")
                            if(authData.isNullOrEmpty())
                            {
                                if(redirect)
                                {
                                    Log.d("MyLog", "Auth redirect to login")
                                    navHostController.navigate("login")
                                }
                            } else
                            {
                                val data: AuthData = gson.fromJson(authData, AuthData::class.java);
                                this.login(data, context, navHostController, redirect)
                                auth_tested.value = AuthStatus.AUTH

                            }

                        } else
                        {
                            auth_tested.value = AuthStatus.AUTH
                        }


                    },
                    Response.ErrorListener { error ->
                        Log.i("AuthLog", "error = " + error)
//                        throw Exception("no connection")
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
    }
}