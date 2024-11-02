package lk.mzpo.ru.network.retrofit

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.BufferedSink
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.io.FileInputStream

interface UploadImage {

    @Multipart
    @POST("mobile/user/uploadDoc")
    fun uploadImage(
        @Header("Authorization") authHeader: String,
        @Part image: MultipartBody.Part,
        @Part("contract") contract: Int,
        @Part("admission") admission: Int,
        @Part("verify") verify: Int? = null,
    ): Call<ResponseBody>

    companion object {
        operator fun invoke(): UploadImage{
            return Retrofit.Builder()
                .baseUrl("https://lk.mzpo-s.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UploadImage::class.java)
        }
    }
}

class UploadRequestBody(
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