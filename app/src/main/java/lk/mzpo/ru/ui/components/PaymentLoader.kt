package lk.mzpo.ru.ui.components

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lk.mzpo.ru.network.retrofit.CheckPaymentStatusRequest
import lk.mzpo.ru.network.retrofit.CheckPaymentStatusResponse
import lk.mzpo.ru.network.retrofit.PurchaseService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


fun checkPaymentStatus(
    orderId: Int,
    token: String?,
    onPaymentSuccess: () -> Unit,
    onPaymentFailed: () -> Unit
) {
    val requestBody = CheckPaymentStatusRequest(orderId)

    CoroutineScope(Dispatchers.IO).launch {
        repeat(10) { attempt ->
            PurchaseService.apiService.checkOrderStatus("Bearer $token", requestBody)
                .enqueue(object : Callback<CheckPaymentStatusResponse> {
                    override fun onResponse(
                        call: Call<CheckPaymentStatusResponse>,
                        response: Response<CheckPaymentStatusResponse>
                    ) {
                        if (response.isSuccessful) {
                            val paymentResponse = response.body()
                            if (paymentResponse?.payment_status == 1) {
                                onPaymentSuccess.invoke()

                            }
                        }
                    }

                    override fun onFailure(call: Call<CheckPaymentStatusResponse>, t: Throwable) {
                        Log.e("Payment", "Network error: ${t.message}")
                        // Обработайте сетевую ошибку
                    }

                })
            delay(3000)
        }
        onPaymentFailed()
    }
}