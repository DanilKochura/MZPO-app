package lk.mzpo.ru.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import lk.mzpo.ru.models.Contract
import lk.mzpo.ru.models.User
import lk.mzpo.ru.network.retrofit.AuthData
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import org.json.JSONArray
import org.json.JSONObject


class ContractsViewModel  (
): ViewModel()
{
    val contracts = mutableStateOf(listOf<Contract>()) // список заказов
    val user = mutableStateOf(User(0,"","", "", ""))
    val selected = mutableStateOf("Активные") // активная вкладка меню

    val auth_tested = mutableStateOf(AuthStatus.TEST) //статус авторизации

    val loaded = mutableStateOf(false) //флаг полной загрузки контента
    val error = mutableStateOf(false) //флаг полной загрузки контента

    val accessDates = mutableListOf("")
    val accessOrder = mutableIntStateOf(0)
    val selectedDate = mutableStateOf("")
    val accessModule = mutableIntStateOf(0)
    /**
     * Получение списка заказов
     * @param token - ЛК AUTH токен
     * @param context - Контекст для очереди
     */
    fun getData(token: String, context: Context)
    {
        val url = "https://lk.mzpo-s.ru/mobile/user/study"
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    Log.d("getData", response)
                    val gson = Gson();
                    val json = JSONArray(response)
                    val array = arrayListOf<Contract>()
                    for (i in 0 until  json.length())
                    {
                        val string = json[i].toString()
                        array.add(gson.fromJson(string, Contract::class.java))
                    }
                    this.contracts.value = array
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
}