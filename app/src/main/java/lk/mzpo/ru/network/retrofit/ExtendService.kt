package lk.mzpo.ru.network.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ExtendRequest {

    @POST("mobile/user/extend/contract")
    fun send(
        @Header("Authorization") authHeader: String,
        @Body postBody: PostBody
    ): Call<ResponseBody>

    companion object {
        operator fun invoke(): ExtendRequest{
            return Retrofit.Builder()
                .baseUrl("https://trayektoriya.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ExtendRequest::class.java)
        }
    }

    data class PostBody(
        val contract_id: Int,
        val exam_date: String,
        val module_id: Int,
    )
}

