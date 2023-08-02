package lk.mzpo.ru

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import lk.mzpo.ru.ui.theme.MzpoTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.network.retrofit.CourseApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

class MainActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

            val nav = rememberNavController()
            MzpoTheme {


                NavGraph(navHostController = nav)
            }
        }
    }
}


