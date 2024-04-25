package lk.mzpo.ru

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
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
import kotlin.math.log


class MainActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
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
                NavGraph(navHostController = nav, cart_sum)
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
}


