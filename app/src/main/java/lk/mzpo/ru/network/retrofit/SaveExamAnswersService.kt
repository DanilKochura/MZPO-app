package lk.mzpo.ru.network.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SaveExamAnswersService {

    @POST("mobile/user/study/saveAnswers")
    fun send(
        @Header("Authorization") authHeader: String,
        @Body postBody: PostBody
    ): Call<ResponseBody>

    companion object {
        operator fun invoke(): SaveExamAnswersService{
            return Retrofit.Builder()
                .baseUrl("https://trayektoriya.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SaveExamAnswersService::class.java)
        }
    }

    data class PostBody(
        val answers: List<String>,
//        val contract: Int,
        val contract_id: Int,
        val module_id: Int,
        val ticket: Int,
    )
}

