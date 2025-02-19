package lk.mzpo.ru.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import lk.mzpo.ru.models.Certificate
import lk.mzpo.ru.models.User
import org.json.JSONArray


class CertificatesViewModel(
) : ViewModel() {
    val certificates = mutableListOf<Certificate>() // список заказов

    val user = mutableStateOf(User(0, "", "", "", ""))

    val loaded = mutableStateOf(false)
    val err = mutableStateOf(false)
    /**
     * Получение заказов
     * @param context - Контекст для очереди
     */
    fun getData(context: Context) {
        certificates.clear()
        val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val token = test.getString("token_lk", "")
        val url = "https://trayektoriya.ru/mobile/user/study/certificates"
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    if (!response.isNullOrEmpty())  {
                        val mainObject = JSONArray(response)
                        val gson = Gson()
                        for (i in 0 until mainObject.length()) {
                            certificates.add(gson.fromJson(mainObject[i].toString(), Certificate::class.java))
                        }
                    }
                    loaded.value = true
                },
                Response.ErrorListener { error ->
                    err.value = true
                    loaded.value = true
                    Log.i("CertsLog", "error = " + error)
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer " + token?.trim('"')
                    return headers
                }

            }
        queue.add(stringReq)
    }


}