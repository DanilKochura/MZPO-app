package lk.mzpo.ru.network.retrofit

import android.net.Uri
import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import okio.BufferedSink
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.io.FileInputStream

interface DemoRegisterRequest {

    @POST("api/mzpo/demo-user")
    fun send(
        @Header("Authorization") authHeader: String,
        @Body postBody: PostBody
    ): Call<ResponseBody>

    companion object {
        operator fun invoke(): DemoRegisterRequest{
            return Retrofit.Builder()
                .baseUrl("https://lk.mzpo-s.ru/")
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

