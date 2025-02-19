package lk.mzpo.ru.network.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

data class PaymentResponse(val status: String, val payment_id: String?, val redirect_url:String?)

interface ApiService {
    @Headers(
        "Cache-Control: no-cache",
        "Cache-Control: no-store",
        "Pragma: no-cache"
    )
    @POST("user/complete-payment-mobile")
    fun completePayment(
        @Header("Authorization") authHeader: String,
        @Body request: PaymentRequest
    ): Call<PaymentResponse>



    @Headers(
        "Cache-Control: no-cache",
        "Cache-Control: no-store",
        "Pragma: no-cache"
    )
    @POST("/payment-status")
    fun checkPaymentStatus(
        @Header("Authorization") authHeader: String,
        @Body request: CheckPaymentRequest
    ): Call<CheckPaymentResponse>
}

data class PaymentRequest(
    val token: String,
    val cartId: Int,
    //    val returnUrl: String
)


object PurchaseService {
    private const val BASE_URL = "https://trayektoriya.ru/mobile/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    val apiService: ApiService by lazy {
        instance.create(ApiService::class.java)
    }
}

data class CheckPaymentResponse(val status: String, val payment_status: String?, val details: String?)


data class CheckPaymentRequest(
    val payment_id: String,
)

