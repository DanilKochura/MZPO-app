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
import lk.mzpo.ru.models.Contract
import lk.mzpo.ru.models.Course
import lk.mzpo.ru.models.Document
import lk.mzpo.ru.models.Group
import lk.mzpo.ru.models.Module
import lk.mzpo.ru.models.User
import lk.mzpo.ru.models.UserData
import lk.mzpo.ru.network.retrofit.AuthData
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class BillsViewModel(
) : ViewModel() {
    val contracts = mutableStateOf(listOf<Contract>()) // список заказов

    val user = mutableStateOf(User(0, "", ""))

    /**
     * Получение заказов
     * @param context - Контекст для очереди
     */
    fun getData(context: Context) {
        val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val token = test.getString("token_lk", "")
        val url = "https://lk.mzpo-s.ru/mobile/user/payments"
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    contracts.value = getContracts(response)
                },
                Response.ErrorListener { error ->
                    Log.i("BillsLog", "error = " + error)
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

    /**
     * Парсинг полученных заказов
     * @param response - JSON-ответ сервера
     */
    private fun getContracts(response: String?): List<Contract> {
        val array = arrayListOf<Contract>()
        if (response.isNullOrEmpty()) return listOf()
        val mainObject = JSONArray(response)
        val gson = Gson()
        for (i in 0 until mainObject.length()) {
            array.add(gson.fromJson(mainObject[i].toString(), Contract::class.java))
        }
        return array
    }



}