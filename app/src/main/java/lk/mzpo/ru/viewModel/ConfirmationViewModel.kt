package lk.mzpo.ru.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import lk.mzpo.ru.models.CartItem
import lk.mzpo.ru.models.study.Group
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.network.retrofit.CartExamService
import lk.mzpo.ru.network.retrofit.CartGroupService
import lk.mzpo.ru.network.retrofit.CartTypeService
import lk.mzpo.ru.network.retrofit.CheckPromocodeRequest
import lk.mzpo.ru.network.retrofit.CheckPromocodeResponse
import lk.mzpo.ru.network.retrofit.PurchaseService
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmationViewModel  (
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

    val cartItem = mutableStateOf<CartItem?>(null)

    val promoCodeText = mutableStateOf("")

    fun getCartItem(id: Int, token: String) {
        PurchaseService.apiService.getCartItem(id, "Bearer $token")
            .enqueue(object : Callback<CartItem> {
                override fun onResponse(
                    call: Call<CartItem>,
                    response: Response<CartItem>
                ) {
                    if (response.isSuccessful) {
                        cartItem.value = response.body()
                    }
                }

                override fun onFailure(call: Call<CartItem>, t: Throwable) {
                    Log.e("Payment", "Network error: ${t.message}")
                }

            })
        }

    fun checkPromocode(token: String, promocode: String, text: MutableState<String?>, isError: MutableState<Boolean>) {
        PurchaseService.apiService.checkPromocode("Bearer $token", CheckPromocodeRequest(cartItem.value!!.course.uid, cartItem.value!!.org_id, promocode, cartItem.value!!.id ))
            .enqueue(object : Callback<CheckPromocodeResponse> {
                override fun onResponse(
                    call: Call<CheckPromocodeResponse>,
                    response: Response<CheckPromocodeResponse>
                ) {
                    if (response.isSuccessful) {
                        val resp = response.body()
                        Log.d("MyLog", response.message().toString())
                        if(resp?.sale !== 0)
                        {
                            getCartItem(cartItem.value!!.id, token)
                        } else
                        {
                            text.value = "Промокод не найден"
                            isError.value = true
                        }
                    }
                }

                override fun onFailure(call: Call<CheckPromocodeResponse>, t: Throwable) {
                    Log.e("Payment", "Network error: ${t.message}")
                }

            })
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