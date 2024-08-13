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
import retrofit2.http.Field
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.io.FileInputStream

interface FeedbackService {

    @Multipart
    @POST("mobile/user/feedback")
    fun sendFeedback(
        @Header("Authorization") authHeader: String,
        @Part image: MultipartBody.Part?,
        @Part("version") version: String,
        @Part("mobile") mobile: String,
        @Part("message") message: String,
        @Part("subject") subject: String,
    ): Call<ResponseBody>

    companion object {
        operator fun invoke(): FeedbackService{
            return Retrofit.Builder()
                .baseUrl("https://lk.mzpo-s.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FeedbackService::class.java)
        }
    }
}

class FeedbackRequestBody(
    private val file: File,
    private val contentType: String,

) : RequestBody() {
    override fun contentType() = "$contentType/*".toMediaTypeOrNull()

    override fun contentLength() = file.length()

    override fun writeTo(sink: BufferedSink) {

        val length = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(file)
        var uploaded = 0L
        fileInputStream.use { inputStream ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (inputStream.read(buffer).also {
                    read = it
                } != -1) {
                uploaded += read
                sink.write(buffer, 0, read)
            }
        }
    }



    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }
}