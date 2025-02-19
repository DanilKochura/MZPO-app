package lk.mzpo.ru.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import lk.mzpo.ru.models.Gift
import lk.mzpo.ru.models.study.ActiveFile
import lk.mzpo.ru.network.retrofit.AuthStatus
import org.json.JSONArray

class GiftViewModel: ViewModel() {

    lateinit var gift: Gift
    val loaded = mutableStateOf(false)
    val error = mutableStateOf(false)

    val auth_tested = mutableStateOf(AuthStatus.TEST)

    val materials = mutableStateOf(listOf<ActiveFile>())


    fun getData(context: Context)
    {
        val url = "https://trayektoriya.ru/mobile/user/gifts/${gift.id}"
        val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val token = test.getString("token_lk", "")
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    val gson = Gson();

                    try {
                        val main = JSONArray(response)
                        val array = arrayListOf<ActiveFile>()
                        for (i in 0 until main.length())
                        {
                            array.add(gson.fromJson(main[i].toString(), ActiveFile::class.java))
                        }
                        this.materials.value = array
                        loaded.value = true
                    } catch (e: Exception)
                    {
                        Log.e("MyLOg", e.toString())
                        loaded.value = true
                        error.value = true
                        return@Listener;
                    }

                },
                Response.ErrorListener {
                        err: VolleyError? ->
                    loaded.value = true
                    if(err?.networkResponse !== null)
                    {
                        Log.e("AAAAA", err.networkResponse.statusCode.toString())
                        if(err.networkResponse.statusCode == 500)
                        {
                            error.value = true
                        }
                    }
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