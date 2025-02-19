package lk.mzpo.ru.network.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface BuyExtendRequest {

    @POST("mobile/user/cart/buy-extend")
    fun send(
        @Header("Authorization") authHeader: String,
        @Body postBody: PostBody
    ): Call<ResponseBody>

    @POST("mobile/user/cart/buy-recovery")
    fun sendRecovery(
        @Header("Authorization") authHeader: String,
        @Body postBody: RecoveryPostBody
    ): Call<ResponseBody>

    companion object {
        operator fun invoke(): BuyExtendRequest{
            return Retrofit.Builder()
                .baseUrl("https://trayektoriya.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BuyExtendRequest::class.java)
        }
    }

    data class PostBody(
        val exam_date: String,
        val module_uid: String,
        val contract_uid: String,
        val course_hours: Int,
    )


}

data class RecoveryPostBody(
    val contract_id: Int,
)