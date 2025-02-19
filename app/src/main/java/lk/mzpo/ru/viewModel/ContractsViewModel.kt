package lk.mzpo.ru.viewModel

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import lk.mzpo.ru.models.Contract
import lk.mzpo.ru.models.Gift
import lk.mzpo.ru.models.User
import lk.mzpo.ru.network.retrofit.AuthStatus
import org.json.JSONObject


class ContractsViewModel  (
): ViewModel()
{
    val contracts = mutableStateOf(listOf<Contract>()) // список заказов
    val gifts = mutableStateOf(listOf<Gift>()) // список заказов
    val user = mutableStateOf(User(0,"","", "", ""))
    val selected = mutableStateOf("Активные") // активная вкладка меню

    val auth_tested = mutableStateOf(AuthStatus.TEST) //статус авторизации

    val loaded = mutableStateOf(false) //флаг полной загрузки контента
    val error = mutableStateOf(false) //флаг полной загрузки контента

    val accessDates = mutableListOf("")
    val accessOrder = mutableIntStateOf(0)
    val selectedDate = mutableStateOf("")
    val accessModule = mutableIntStateOf(0)

    val accessModuleUid = mutableStateOf("")
    val accessContractUid = mutableStateOf("")
    val accessCourseHours = mutableStateOf(0)
    val accessPrice = mutableStateOf(0)

    val accessFree = mutableStateOf("1")
    /**
     * Получение списка заказов
     * @param token - ЛК AUTH токен
     * @param context - Контекст для очереди
     */
    fun getData(token: String, context: Context)
    {
        val url = "https://trayektoriya.ru/mobile/user/study"
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    try {
                        val gson = Gson();
                        val common = JSONObject(response)
                        val json = common.getJSONArray("contracts")
                        val array = arrayListOf<Contract>()
                        for (i in 0 until  json.length())
                        {
                            val string = json[i].toString()
                            array.add(gson.fromJson(string, Contract::class.java))
                        }
                        this.contracts.value = array


                        val jsong = common.getJSONArray("gifts")
                        val arrayg = arrayListOf<Gift>()
                        for (i in 0 until  jsong.length())
                        {
                            val string = jsong[i].toString()
                            arrayg.add(gson.fromJson(string, Gift::class.java))
                        }
                        this.gifts.value = arrayg
                    } catch (_:Exception)
                    {
                        error.value = true
                    }


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