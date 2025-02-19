package lk.mzpo.ru.network.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DemoRegisterRequest {

    @POST("api/mzpo/demo-user")
    fun send(
        @Header("Authorization") authHeader: String,
        @Body postBody: PostBody
    ): Call<ResponseBody>

    companion object {
        operator fun invoke(): DemoRegisterRequest{
            return Retrofit.Builder()
                .baseUrl("https://trayektoriya.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DemoRegisterRequest::class.java)
        }
    }

    data class PostBody(
        val name: String,
        val email: String,
        val phone: String,
        val demo_key: String = "AnWqKt8xSkQlTPI",
    )
}

