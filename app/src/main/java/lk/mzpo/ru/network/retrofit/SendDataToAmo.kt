package lk.mzpo.ru.network.retrofit

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class SendDataToAmo {

    companion object
    {
        fun sendDataToAmo(data: Data2Amo, ctx: Context)
        {
            val queue = Volley.newRequestQueue(ctx)

            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                "https://mzpo2amo.ru/siteform/retail",
                Response.Listener { response ->
                    Log.d("MyLog", response.toString())
                    Toast.makeText(ctx, "Заявка оставлена!", Toast.LENGTH_SHORT).show()
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
                    params["name"] = data.name
                    params["form_name_site"] = data.form_name_site
                    params["email"] = data.email
                    params["phone"] = data.phone
                    if(data.comment != "")
                    {
                        params["comment"] = data.comment
                    } else
                    {
                        params["comment"] = data.form_name_site
                    }
                    Log.d("MyLog", params.toString())
                    return params
                }
            }

            queue.add(stringRequest)


        }
    }
}