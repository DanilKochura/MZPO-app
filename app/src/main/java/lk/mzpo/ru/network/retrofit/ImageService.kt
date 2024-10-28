package lk.mzpo.ru.network.retrofit

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ImageService {

    @GET("mobile/user/images/{imageId}")
    fun getImage(
        @Header("Authorization") token: String,
        @Path("imageId") imageId: String
    ): Call<ResponseBody>

    companion object {
        operator fun invoke(): ImageService {
            val client = OkHttpClient.Builder()
                .addInterceptor(LoggingInterceptor()) // Добавляем интерцептор
                .build()
            Log.d("MyLog", this.toString())
            return Retrofit.Builder()
                .baseUrl("https://lk.mzpo-s.ru/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ImageService::class.java)
        }
    }
}


class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // Логируем URL
        Log.d("Retrofit", "Request URL: ${request.url}")
        val newUrl = request.url.toString().replace("%2F", "/") // Пример ручной замены
        val newRequest = request.newBuilder()
            .url(newUrl)
            .build()
        // Пропускаем запрос дальше
        Log.d("Retrofit", "Request URL: ${newRequest.url}")

        return chain.proceed(newRequest)
    }
}