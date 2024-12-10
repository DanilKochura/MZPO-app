package lk.mzpo.ru.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class VideoViewModel  (
): ViewModel() {


    fun update(
        ctx: Context,
        id: Int,
        time: Float,
        progress: Int
    ) {

        val queue = Volley.newRequestQueue(ctx)
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://lk.mzpo-s.ru/mobile/user/study/saveTime/$id",
            Response.Listener { response ->
                Log.d("MyLog", response)
//                Log.d("MyLog", "https://lk.mzpo-s.ru/mobile/user/study/saveTime/$id")
            },

            Response.ErrorListener { error ->
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
                val test = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
                val token = test.getString("token_lk", "")
                headers["Authorization"] = "Bearer " + token?.trim('"')
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                params["time"] = time.toString()
                params["progress"] = progress.toString()
//                Log.d("MyLog", params.toString());
                return params
            }
        }
        stringRequest.setShouldCache(false);
        queue.add(stringRequest)


    }

}