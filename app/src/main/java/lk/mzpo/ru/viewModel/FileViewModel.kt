package lk.mzpo.ru.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import lk.mzpo.ru.models.study.NewMaterials

class FileViewModel  (): ViewModel() {


    val loaded = mutableStateOf(false)
    val error = mutableStateOf(false)
    val material: MutableState<NewMaterials?> = mutableStateOf(null)

    fun getData(context: Context, contract_id: Int, moduleId: Int, material_id: Int)
    {
        val url = "https://trayektoriya.ru/mobile/v2/user/contract/${contract_id}/module/${moduleId}/material/${material_id}"
        val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val token = test.getString("token_lk", "")
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    val gson = Gson();

                    try {
                        this.material.value = gson.fromJson(response, NewMaterials::class.java)
                        loaded.value = true
                    } catch (e: Exception)
                    {
                        loaded.value = true
                        error.value = true
                        Log.e("MYLOG", e.message.toString())
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

