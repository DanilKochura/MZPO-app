package lk.mzpo.ru.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import lk.mzpo.ru.models.CartItem
import lk.mzpo.ru.models.study.Group
import lk.mzpo.ru.network.firebase.FirebaseHelpers
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.network.retrofit.CartExamService
import lk.mzpo.ru.network.retrofit.CartGroupService
import lk.mzpo.ru.network.retrofit.CartTypeService
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call

class CartViewModel  (
): ViewModel()
{
    val payment_id = mutableStateOf("")
    val payment_status = mutableStateOf(0)
    val courses = mutableStateListOf<CartItem>()  // список курсов в корзине
    val showLoader = mutableStateOf(false)
    val auth_tested = mutableStateOf(AuthStatus.TEST) //статус аунтификации

    val selected_course = mutableStateOf(0)
    val selected_course_org = mutableStateOf(0)

    val selectedCourseIndex = mutableStateOf(0)
    val selectedType = mutableStateOf("")
    val selectedPrice = mutableStateOf(0)
    val selectedGroup = mutableStateOf(0)
    val selectedDate = mutableStateOf("")

    val showDialog =   mutableStateOf(false)
    val orderId = mutableStateOf<Int?>(null)
    val groups = mutableStateListOf<Group>()
    val confirmationURL = mutableStateOf<String?>(null)

    /**
     * Получение списка курсов по ID в ЛК
     * @param context - Контекст для очереди
     * @param cartList - Хеш-карта добавленных в корзину курсов
     */
    fun getCourses(context: Context, cartList: MutableState<List<HashMap<String, String>>> = mutableStateOf(listOf( hashMapOf("" to ""))))
    {
        var ids: String = "?"
        Log.d("CartLog", cartList.value.joinToString(", "))
        var i = 1;
        for( cart in cartList.value )
        {
            Log.d("MyLog", cart["id"].toString())
            if (cart["id"] == null || cart["type"] == null) continue
            ids += "id[$i]="+cart["id"]+"&type[$i]="+cart["type"]+"&"
            i++
        }
//        ids.trimEnd(',')
        Log.d("CartLog", "https://trayektoriya.ru/mobile/courses$ids")

        val url1 = "https://trayektoriya.ru/mobile/courses$ids"
        val queue = Volley.newRequestQueue(context)
        val sRequest = StringRequest(
            Request.Method.GET,
            url1,
            {
                    response ->

                val mainobj = JSONObject(response).getJSONArray("data")
                Log.d("CartLog", response)
                for(i in 0 until  mainobj.length())
                {
                    try {
                        val item = mainobj[i].toString()
                        val gson = Gson();
                        val cartItem = gson.fromJson(item, CartItem::class.java)
                        courses.add(cartItem)
                    } catch (e: Exception) {
                        Log.e("MyLog", e.message.toString())
                    }
                }

            },
            {
                Log.d("MyLog", "VolleyError: $it")
            }
        )
        queue.add(sRequest)




    }

    /**
     * Получение корзины для авторизованных пользователей
     * @param ctx - Контекст для очереди
     */
    fun getLkCart(ctx: Context)
    {
        val queue = Volley.newRequestQueue(ctx)

        val stringRequest: StringRequest = object : StringRequest(
            Method.GET,
            "https://trayektoriya.ru/mobile/user/cart",
            Response.Listener { response ->
                    Log.d("MyLog", response)
                val jsonArray = JSONArray(response)
                val gson = Gson();
                for (i in 0 until jsonArray.length())
                {
                    courses.add(gson.fromJson(jsonArray[i].toString(), CartItem::class.java))
                }


            },

            Response.ErrorListener { error ->
                Toast.makeText(ctx, "Произошла ошибка. Попробуйте позже", Toast.LENGTH_SHORT).show()
                Log.d("MyLog", error.localizedMessage?.toString() ?: "")
                error.printStackTrace()
            }) {

            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> =
                    HashMap()
//                headers["Content-Type"] = "application/x-www-form-urlencoded"
                val test = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
                val token = test.getString("token_lk", "")
                Log.d("MyLog", "Bearer "+token?.trim('"'))
                headers["Authorization"] = "Bearer "+token?.trim('"')
                return headers
            }
        }

        queue.add(stringRequest)

    }


    /**
     * Удаление элемента корзины для авторизованных пользователей
     * @param ctx - Контекст для очереди
     * @param id - ЛК ID курса для удаления
     * @param token - ЛК AUTH токен (необязательный параметр)
     */
    fun deleteLkCart(ctx: Context, id: Int, token: String = "")
    {
        var _token =token
        if(_token.isNullOrEmpty())
        {
            val test = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
            _token = test.getString("token_lk", "").toString()
        }
        val queue = Volley.newRequestQueue(ctx)

        val stringRequest: StringRequest = object : StringRequest(
            Method.DELETE,
            "https://trayektoriya.ru/mobile/user/cart/delete/$id",
            Response.Listener { response ->
                    Log.d("MyLog", response)

                courses.removeIf{it.id == id}


            },

            Response.ErrorListener { error ->
                Toast.makeText(ctx, "Произошла ошибка. Попробуйте позже", Toast.LENGTH_SHORT).show()
                Log.d("MyLog", error.localizedMessage?.toString() ?: "")
                error.printStackTrace()
            }) {

            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> =
                    HashMap()
//                headers["Content-Type"] = "application/x-www-form-urlencoded"
                Log.d("MyLog", "Bearer "+ _token.trim('"'))
                headers["Authorization"] = "Bearer "+ _token.trim('"')
                return headers
            }
        }

        queue.add(stringRequest)

    }

    /**
     * Статические методы
     */
    companion object
    {
        /**
         * Добавление в корзину ЛК курса
         * @param context - Контекст для очереди
         * @param id - ЛК ID курса для добавления
         */
        fun addToCart(context: Context, id: Int, group_id : Int? = null)
        {
            val queue = Volley.newRequestQueue(context)

            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                "https://trayektoriya.ru/mobile/user/cart/add",
                Response.Listener { response ->
                },

                Response.ErrorListener { error ->
                    Toast.makeText(context, "Произошла ошибка. Попробуйте позже", Toast.LENGTH_SHORT).show()
                    error.printStackTrace()
                }) {

                override fun getBodyContentType(): String {
                    return "application/x-www-form-urlencoded"
                }

                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> =
                        HashMap()
                    headers["Content-Type"] = "application/x-www-form-urlencoded"
                    val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                    val token = test.getString("token_lk", "")
                    headers["Authorization"] = "Bearer "+token?.trim('"')
                    return headers
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> =
                        HashMap()
                    params["course_id"] = id.toString()
                    if(group_id !== null)
                    {
                        params["group_id"] = group_id.toString()
                    }
                    return params
                }
            }

            queue.add(stringRequest)

        }

        /**
         * Получение количества товаров в корзине ЛК
         * @param context - Контекст для очереди
         * @param sum - Счетчик товаров
         */
        fun getLkCartSum(ctx: Context, sum: MutableState<Int>)
        {
            val queue = Volley.newRequestQueue(ctx)

            val stringRequest: StringRequest = object : StringRequest(
                Method.GET,
                "https://trayektoriya.ru/mobile/user/cart/sum",
                Response.Listener { response ->
                    try {
                        val json = JSONArray(response)
                        sum.value = json[0].toString().toInt()
                        Log.d("CartLog", sum.value.toString())
                    } catch (_: Exception) {}
                },

                Response.ErrorListener { error ->
                    Toast.makeText(ctx, "Произошла ошибка. Попробуйте позже", Toast.LENGTH_SHORT).show()
                    Log.d("MyLog", error.localizedMessage?.toString() ?: "")
                    error.printStackTrace()
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> =
                        HashMap()
//                headers["Content-Type"] = "application/x-www-form-urlencoded"
                    val test = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
                    val token = test.getString("token_lk", "")
                    Log.d("MyLog", "Bearer "+token?.trim('"'))
                    headers["Authorization"] = "Bearer "+token?.trim('"')
                    return headers
                }
            }

            queue.add(stringRequest)

        }
    }

    /**
     * Удаление курса из корзины
     * @param context - Контекст для очереди
     * @param id - ЛК ID курса на удаление
     */
    fun deleteCartItem(context: Context, id: Int)
    {
        if (auth_tested.value == AuthStatus.AUTH) // если авторизован, то удаление из корзины ЛК
        {
                deleteLkCart(context, id)
        } else //если нет, то отправляю в Firebase
        {
            val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
            val token = test.getString("token", "")
            FirebaseHelpers.deleteFromCart(token, id)
        }
    }




    fun updateType(context: Context, token: String?, cart_id: Int, type: String){
        CartTypeService().send(
            "Bearer " + token?.trim('"'),
            CartTypeService.PostBody(
                cart_id, type
            )

        ).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(
                call: Call<ResponseBody>,
                t: Throwable
            ) {
                Log.e(
                    "API Request",
                    "I got an error and i don't know why :("
                )
                Log.e("API Request", t.message.toString())
                Toast.makeText(
                    context,
                    "Произошла ошибка. Попробуйте позже!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                response.body()?.let { responseBody ->
                    val responseBodyString =
                        responseBody.string()
                    Log.d("API Response", responseBodyString)
                    if (response.isSuccessful) {
                        try {
                            val mainobj = JSONObject(responseBodyString)
                            groups.clear()
                            val groupsJSON = mainobj.getJSONArray("groups")
                            val gson = Gson()
                            for (i in 0 until groupsJSON.length())
                            {
                                groups.add(gson.fromJson(groupsJSON[i].toString(), Group::class.java))
                            }
                        } catch (_: Exception){}
                    }
                }

            }
        })
    }


    fun updateGroup(context: Context, token: String?, cart_id: Int, group_id: Int){
        Log.d("MyLog", "$cart_id $group_id")
        CartGroupService().send(
            "Bearer " + token?.trim('"'),
            CartGroupService.PostBody(
                cart_id, group_id
            )

        ).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(
                call: Call<ResponseBody>,
                t: Throwable
            ) {
                Log.e(
                    "API Request",
                    "I got an error and i don't know why :("
                )
                Log.e("API Request", t.message.toString())
                Toast.makeText(
                    context,
                    "Произошла ошибка. Попробуйте позже!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                response.body()?.let { responseBody ->
                    val responseBodyString =
                        responseBody.string()
                    Log.d("API Response", responseBodyString)
                    if (response.isSuccessful)
                    {
                        selectedGroup.value = group_id
                        Toast.makeText(context, "Корзина обновлена!", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        })
    }

    fun updateDate(context: Context, token: String?, courseId: Int, date: String) {
        CartExamService().send(
            "Bearer " + token?.trim('"'),
            CartExamService.PostBody(
                courseId, date
            )

        ).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(
                call: Call<ResponseBody>,
                t: Throwable
            ) {
                Log.e(
                    "API Request",
                    "I got an error and i don't know why :("
                )
                Log.e("API Request", t.message.toString())
                Toast.makeText(
                    context,
                    "Произошла ошибка. Попробуйте позже!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                response.body()?.let { responseBody ->
                    val responseBodyString =
                        responseBody.string()
                    Log.d("API Response", responseBodyString)
                    if (response.isSuccessful)
                    {
                        selectedDate.value = date
                        Toast.makeText(context, "Корзина обновлена!", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        })
    }

}