package lk.mzpo.ru.viewModel

import android.content.Context
import android.util.Log
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
import org.json.JSONArray
import org.json.JSONObject


class ContractsViewModel  (
): ViewModel()
{
    val courses = mutableStateOf(listOf<Contract>())

    val user = mutableStateOf(User(0,"",""))
    val selected = mutableStateOf("Активные")

    val auth_tested = mutableStateOf(false)
    fun getData(token: String, context: Context)
    {
        val url = "https://lk.mzpo-s.ru/mobile/user/study"
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    val gson = Gson();
                    val json = JSONArray(response)
                    val array = arrayListOf<Contract>()
                    for (i in 0 until  json.length())
                    {
                        val string = json[i].toString()
                        array.add(gson.fromJson(string, Contract::class.java))
                    }
                    this.courses.value = array
                },
                Response.ErrorListener { error ->
                    Log.i("mylog", "error = " + error)
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

    fun getUser(response: String): User {


        if (response.isEmpty()) return User(0,"","")
        val mainObject = JSONObject(response)
        return User(
            mainObject.getInt("id"),
            mainObject.getString("name"),
            mainObject.getString("email")
        )

    }


    fun testAuth(context: Context, token: String, navHostController: NavHostController){
        val url = "https://lk.mzpo-s.ru/api/testAuth"
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    val resp = JSONArray(response)
                    if(resp.getString(0).equals("guest"))
                    {
                        val test =  context.getSharedPreferences("session", Context.MODE_PRIVATE)
                        val gson = Gson()
                        val data: AuthData = gson.fromJson(test.getString("auth_data", ""), AuthData::class.java);
                        AuthService.login(data, context, navHostController)
                    }
                    auth_tested.value = true

                },
                Response.ErrorListener { error ->
                    Log.i("mylog", "error = " + error)
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