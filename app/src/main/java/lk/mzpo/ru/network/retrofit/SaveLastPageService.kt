package lk.mzpo.ru.network.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SaveLastPageService {

    @POST("mobile/user/study/saveLastPage")
    fun send(
        @Header("Authorization") authHeader: String,
        @Body postBody: PostBody
    ): Call<ResponseBody>

    companion object {
        operator fun invoke(): SaveLastPageService{
            return Retrofit.Builder()
                .baseUrl("https://lk.mzpo-s.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SaveLastPageService::class.java)
        }
    }

    data class PostBody(
//        val material: Int,
//        val contract: Int,
        val page: Int,
        val progress: Int,
    )
}

