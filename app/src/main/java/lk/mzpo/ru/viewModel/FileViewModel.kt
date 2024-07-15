package lk.mzpo.ru.viewModel

import CoursePreview
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lk.mzpo.ru.models.Course
import lk.mzpo.ru.models.Document
import lk.mzpo.ru.models.Group
import lk.mzpo.ru.models.Module
import lk.mzpo.ru.models.User
import lk.mzpo.ru.models.UserData
import lk.mzpo.ru.network.retrofit.AuthData
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.log
import okhttp3.*
import java.io.*

class FileViewModel  (
): ViewModel() {




}

