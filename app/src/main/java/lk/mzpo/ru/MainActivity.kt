package lk.mzpo.ru

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import lk.mzpo.ru.network.firebase.FirebaseHelpers
import lk.mzpo.ru.ui.theme.MzpoTheme
import kotlin.math.log


class MainActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics

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
            MzpoTheme {
                NavGraph(navHostController = nav)
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


