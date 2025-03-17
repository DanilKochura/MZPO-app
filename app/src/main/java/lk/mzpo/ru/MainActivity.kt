package lk.mzpo.ru

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import io.sanghun.compose.video.cache.VideoPlayerCacheManager
import lk.mzpo.ru.network.HttpsTrustManager
import lk.mzpo.ru.network.firebase.FirebaseHelpers
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.services.CustomTabHelper
import lk.mzpo.ru.ui.theme.MzpoTheme
import lk.mzpo.ru.viewModel.CartViewModel
import ru.yoomoney.sdk.kassa.payments.Checkout
import ru.yoomoney.sdk.kassa.payments.Checkout.createConfirmationIntent
import ru.yoomoney.sdk.kassa.payments.Checkout.createTokenizationResult
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType


class MainActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var customTabHelper: CustomTabHelper

    fun start3DSecure() {
        val intent = createConfirmationIntent(
            this,
            "https://3dsurl.com/",
            PaymentMethodType.BANK_CARD,
            "clientApplicationKey",
            "shopId"
        )
        startActivityForResult(intent, 1)
    }

    fun startConfirmSberPay() {
        val intent = createConfirmationIntent(
            this,
            "your_app_scheme://invoicing/sberpay",
            PaymentMethodType.SBERBANK,
            "clientApplicationKey",
            "shopId"
        )
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            when (resultCode) {
                RESULT_OK -> {
                    // Процесс 3ds завершён, нет информации о том, завершился процесс с успехом или нет
                    // Рекомендуется запросить статус платежа
                    return
                }
                RESULT_CANCELED -> return // Экран 3ds был закрыт
                Checkout.RESULT_ERROR -> {
                    // Во время 3ds произошла какая-то ошибка (нет соединения или что-то еще)
                    // Более подробную информацию можно посмотреть в data
                    // data.getIntExtra(Checkout.EXTRA_ERROR_CODE) - код ошибки из WebViewClient.ERROR_* или Checkout.ERROR_NOT_HTTPS_URL
                    // data.getStringExtra(Checkout.EXTRA_ERROR_DESCRIPTION) - описание ошибки (может отсутствовать)
                    // data.getStringExtra(Checkout.EXTRA_ERROR_FAILING_URL) - url по которому произошла ошибка (может отсутствовать)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume called")
        customTabHelper.handleReturnToCart() // Проверяем, вернулся ли пользователь
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy called")
        if (::customTabHelper.isInitialized) {
            customTabHelper.unbindService()
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        customTabHelper = CustomTabHelper(this) {

        }
        adjustFontScale(resources.configuration);
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            this.getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//
//            )
//
//        }
        analytics = Firebase.analytics

        WindowCompat.setDecorFitsSystemWindows(window, false)

        HttpsTrustManager.allowAllSSL();
        setContent {
            val context = LocalContext.current



            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("myLog111", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
//                Log.d("myLog111", "TOKEN:" + token)
                // Log and toast
//            sendToken(token, context)
                FirebaseHelpers.createOrUpdateToken(token, context)
                val test = getSharedPreferences("session", Context.MODE_PRIVATE)
                test.edit().putString("token", token.toString()).apply()

            })
            val nav = rememberNavController()
            val auth_tested = remember {
                mutableStateOf(AuthStatus.TEST)
            }
            val cart_sum = remember {
                mutableStateOf(0)
            }
            LaunchedEffect(key1 = "test", block = {
                AuthService.testAuth(context, nav, auth_tested, false);
            })
            LaunchedEffect(key1 = auth_tested.value , block = {
                if(auth_tested.value == AuthStatus.AUTH)
                {
                    CartViewModel.getLkCartSum(context, cart_sum)
                }
            })


            MzpoTheme {
                NavGraph(navHostController = nav, cart_sum, customTabHelper)
            }
        }


    }

    private fun adjustFontScale(configuration: Configuration) {
        if (configuration.fontScale > 1.30) {

            configuration.fontScale = 1.0f
            val metrics = resources.displayMetrics
            val wm = getSystemService(WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            metrics.scaledDensity = configuration.fontScale * metrics.density
            baseContext.resources.updateConfiguration(configuration, metrics)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action
        val data: Uri? = intent.data

        if (Intent.ACTION_VIEW == action && data != null) {
            val path = data.pathSegments
            // Обработка данных из диплинка
            Log.d("DeepLink", "Path: $path")
        }
    }
}


