package lk.mzpo.ru

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import lk.mzpo.ru.network.HttpsTrustManager
import lk.mzpo.ru.network.firebase.FirebaseHelpers
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.ui.theme.MzpoTheme
import lk.mzpo.ru.viewModel.CartViewModel
import ru.yoomoney.sdk.kassa.payments.Checkout
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.GooglePayParameters
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentParameters
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.SavePaymentMethod
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.TestParameters
import java.math.BigDecimal
import java.util.Currency
import java.util.Locale
import kotlin.math.log


class OrderActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text(text = "test")
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_CODE_TOKENIZE) {
//            when (resultCode) {
//                Activity.RESULT_OK -> showToken(data)
//                Activity.RESULT_CANCELED -> showError()
//            }
//        }
//    }

    private fun initUi() {
        
    }

    private fun onTokenizeButtonCLick() {
        val paymentMethodTypes = setOf(
            PaymentMethodType.GOOGLE_PAY,
            PaymentMethodType.BANK_CARD,
            PaymentMethodType.SBERBANK,
            PaymentMethodType.YOO_MONEY,
            PaymentMethodType.SBP,
        )
//        val paymentParameters = PaymentParameters(
//            amount = Amount(BigDecimal.valueOf(10.0), Currency.getInstance("RUB")),
//            title = getString(R.string.main_product_name),
//            subtitle = getString(R.string.main_product_description),
//            clientApplicationKey = BuildConfig.MERCHANT_TOKEN,
//            shopId = BuildConfig.SHOP_ID,
//            savePaymentMethod = SavePaymentMethod.OFF,
//            paymentMethodTypes = paymentMethodTypes,
//            gatewayId = BuildConfig.GATEWAY_ID,
//            customReturnUrl = getString(R.string.test_redirect_url),
//            userPhoneNumber = getString(R.string.test_phone_number),
//            googlePayParameters = GooglePayParameters(),
//            authCenterClientId = BuildConfig.CLIENT_ID
//        )
//
//        val intent =
//            Checkout.createTokenizeIntent(this, paymentParameters, TestParameters(showLogs = true))
//        startActivityForResult(intent, REQUEST_CODE_TOKENIZE)
    }

//    private fun showToken(data: Intent?) {
//        if (data != null) {
//            val token = Checkout.createTokenizationResult(data).paymentToken
//            Toast.makeText(
//                this,
//                String.format(Locale.getDefault(), getString(R.string.tokenization_success), token),
//                Toast.LENGTH_LONG
//            ).show()
//        } else {
//            showError()
//        }
//    }
//
//    private fun showError() {
//        Toast.makeText(this, R.string.tokenization_canceled, Toast.LENGTH_SHORT).show()
//    }

    companion object {
        private const val REQUEST_CODE_TOKENIZE = 1
    }
}


