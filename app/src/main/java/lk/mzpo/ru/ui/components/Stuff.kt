package lk.mzpo.ru.ui.components

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.DatePickerDialog
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import lk.mzpo.ru.BuildConfig
import lk.mzpo.ru.MainActivity
import lk.mzpo.ru.R
import lk.mzpo.ru.network.retrofit.UploadImage
import lk.mzpo.ru.network.retrofit.UploadRequestBody
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.ui.theme.Transparent_Green
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date


@Composable
fun RedButton(onClick: () -> Unit, unit: @Composable () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Aggressive_red)
    ) {
        Unit
    }
}

@Composable
fun EmailTextField(
    email: MutableState<TextFieldValue>,
    isError: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    readonly: Boolean = false
) {
    OutlinedTextField(
        value = email.value,
        placeholder = { Text(text = "courses@example.com", Modifier.alpha(0.5f)) },
        onValueChange = { email.value = it },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email Icon"
            )
        },
        label = { Text(text = "Email") },
        modifier = modifier.padding(top = 10.dp),
        colors = customTextFieldBorderColor(),
        isError = isError.value,
        singleLine = true,
        readOnly = readonly,
        enabled = !readonly,

        )
}

@Composable
fun PasswordTextField(
    password: MutableState<TextFieldValue>,
    isError: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    val pwtr = remember {
        mutableStateOf(false)
    }
    OutlinedTextField(
        value = password.value,
        placeholder = { Text(text = "Введите пароль", Modifier.alpha(0.5f)) },
        onValueChange = { password.value = it },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_vpn_key_24),
                contentDescription = "Password Icon"
            )
        },
        label = { Text(text = "Пароль") },
        modifier = Modifier.padding(top = 10.dp),
        colors = customTextFieldBorderColor(),
        isError = isError.value,
        singleLine = true,
        visualTransformation = if (!pwtr.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = {
                pwtr.value = !pwtr.value
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_visibility_24),
                    contentDescription = "password_eye"
                )
            }
        }
    )
}

@Composable
fun PriceTextField(
    price: MutableState<String>,
    placeholder: String,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = price.value,
        placeholder = { Text(text = placeholder, Modifier.alpha(0.5f)) },
        onValueChange = { price.value = it },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(text = label) },
        modifier = modifier.padding(top = 10.dp),
        colors = customTextFieldBorderColor(),
        singleLine = true,

        )
}

@Composable
fun PhoneTextField(
    phone: MutableState<String>,
    isError: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    readonly: Boolean = false
) {
    val mask = "+7(000)-000-00-00"
    val maskNumber = '0'
    OutlinedTextField(
        value = phone.value,
        placeholder = { Text(text = "+7-988-281-14-07", Modifier.alpha(0.5f)) },
        onValueChange = { phone.value = it.take(mask.count { it == maskNumber }) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Email Icon"
            )
        },
        label = { Text(text = "Телефон") },
        modifier = modifier.padding(top = 10.dp),
        colors = customTextFieldBorderColor(),
        visualTransformation = PhoneVisualTransformation(mask, maskNumber),
        isError = isError.value,
        singleLine = true,
        readOnly = readonly,
        enabled = !readonly,
    )
}


@Composable
fun NameTextField(
    name: MutableState<TextFieldValue>,
    isError: MutableState<Boolean> = mutableStateOf(false),
    modifier: Modifier = Modifier,
    readonly: Boolean = false
) {
    OutlinedTextField(
        value = name.value,
        placeholder = { Text(text = "Иванов Иван Иванович", Modifier.alpha(0.5f)) },
        onValueChange = { name.value = it },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Email Icon"
            )
        },
        label = { Text(text = "ФИО") },
        modifier = modifier.padding(top = 10.dp),
        colors = customTextFieldBorderColor(),
        isError = isError.value,
        singleLine = true,
        readOnly = readonly,
        enabled = !readonly,
    )
}

@Composable
fun CustomTextField(
    name: String,
    placeholder: String,
    icon: ImageVector? = null,
    value: MutableState<TextFieldValue>,
    isError: MutableState<Boolean> = mutableStateOf(false),
    modifier: Modifier = Modifier,
    readonly: Boolean = false
) {
    OutlinedTextField(
        value = value.value,
        placeholder = { Text(text = placeholder, Modifier.alpha(0.5f)) },
        onValueChange = { value.value = it },
//        leadingIcon = {
//            if (icon != null)
//            {
//                Icon(
//                    imageVector = icon,
//                    contentDescription = placeholder+" Icon"
//                )
//            }
//            else
//            {
//                null
//            }
//        },
        label = { Text(text = name) },
        modifier = modifier.padding(top = 10.dp),
        colors = customTextFieldBorderColor(),
        isError = isError.value,
        singleLine = true,
        readOnly = readonly,
        enabled = !readonly,
    )
}

@Composable
fun DateTextField(
    name: String,
    icon: ImageVector? = null,
    value: MutableState<TextFieldValue>,
    isError: MutableState<Boolean> = mutableStateOf(false),
    modifier: Modifier = Modifier,
    readonly: Boolean = false
) {
    OutlinedTextField(
        value = value.value,
        placeholder = { Text(text = "2020-02-10", Modifier.alpha(0.5f)) },
        onValueChange = { value.value = it },
//        leadingIcon = {
//            if (icon != null)
//            {
//                Icon(
//                    imageVector = icon,
//                    contentDescription = placeholder+" Icon"
//                )
//            }
//            else
//            {
//                null
//            }
//        },
        label = { Text(text = name) },
        modifier = modifier.padding(top = 10.dp),
        colors = customTextFieldBorderColor(),
        isError = isError.value,
        singleLine = true,
        readOnly = readonly,
        enabled = !readonly,
        visualTransformation = PhoneVisualTransformation("0000-00-00", '0'),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}


class DateTransformation() : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return dateFilter(text)
    }
}

fun dateFilter(text: AnnotatedString): TransformedText {

    val trimmed = if (text.text.length >= 8) text.text.substring(0..10) else text.text
    Log.d("MyLog", trimmed)
    var out = ""
    for (i in trimmed.indices) {
        out += trimmed[i]

    }

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 3) return offset
            if (offset <= 5) return offset + 1
            if (offset <= 8) return offset + 2
            return 10
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 5) return offset
            if (offset <= 8) return offset - 1
            if (offset <= 10) return offset - 2
            return 8
        }
    }

    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
}

@Composable
fun customTextFieldBorderColor(): TextFieldColors {
    return TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Primary_Green,
        cursorColor = Color.Black,
        unfocusedBorderColor = Color.LightGray,
        disabledBorderColor = Color.Gray
    )
}

class PhoneVisualTransformation(val mask: String, val maskNumber: Char) : VisualTransformation {

    //    private val maxLength = mask.count { it == maskNumber }
    private val maxLength = 10

    override fun filter(text: AnnotatedString): TransformedText {
        Log.d("MyLog", text.text.toString())
        val trimmed = if (text.length >= maxLength) text.take(maxLength) else text
        Log.d("MyLog", trimmed.toString())


        val annotatedString = buildAnnotatedString {
            if (trimmed.isEmpty()) return@buildAnnotatedString

            var maskIndex = 0
            var textIndex = 0
            while (textIndex < trimmed.length && maskIndex < mask.length) {
                if (mask[maskIndex] != maskNumber) {
                    val nextDigitIndex = mask.indexOf(maskNumber, maskIndex)
                    append(mask.substring(maskIndex, nextDigitIndex))
                    maskIndex = nextDigitIndex
                }
                append(trimmed[textIndex++])
                maskIndex++
            }
        }
        Log.d("MyLog", "$annotatedString $mask $maskNumber")
        return TransformedText(annotatedString, PhoneOffsetMapper(mask, maskNumber))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhoneVisualTransformation) return false
        if (mask != other.mask) return false
        if (maskNumber != other.maskNumber) return false
        return true
    }

    override fun hashCode(): Int {
        return mask.hashCode()
    }
}

private class PhoneOffsetMapper(val mask: String, val numberChar: Char) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        var noneDigitCount = 0
        var i = 0
        while (i < offset + noneDigitCount) {
            if (mask[i++] != numberChar) noneDigitCount++
        }
        return offset + noneDigitCount
    }

    override fun transformedToOriginal(offset: Int): Int =
        offset - mask.take(offset).count { it != numberChar }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    return email.matches(emailRegex)
}

@Composable
fun DashedDivider(
    thickness: Dp,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    phase: Float = 10f,
    intervals: FloatArray = floatArrayOf(10f, 10f),
    modifier: Modifier
) {
    Canvas(
        modifier = modifier
    ) {
        val dividerHeight = thickness.toPx()
        drawRoundRect(
            color = color,
            style = Stroke(
                width = dividerHeight,
                pathEffect = PathEffect.dashPathEffect(
                    intervals = intervals,
                    phase = phase
                )
            )
        )
    }
}

fun checkPhone(phone: String): String {
    if (phone.startsWith("+7")) {
        return phone.substring(2)
    } else if (phone.startsWith("8")) {
        return phone.trimStart('8')
    } else return phone
}


@Composable
fun DatePickerDemo(context: Context) {
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    val now = Calendar.getInstance()
    mYear = now.get(Calendar.YEAR)
    mMonth = now.get(Calendar.MONTH)
    mDay = now.get(Calendar.DAY_OF_MONTH)
    now.time = Date()
    val date = remember { mutableStateOf("") }
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
//            date.value = Calendar.DATE.toString()
            date.value = cal.time.toLocaleString()
        }, mYear, mMonth, mDay
    )

    val day1 = Calendar.getInstance()
    day1.set(Calendar.DAY_OF_MONTH, 1)
    datePickerDialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            datePickerDialog.show()
        }) {
            Text(text = "Click To Open Date Picker")
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = "Selected date: ${date.value}")
    }
}


@Composable
fun PieChart(
    data: Map<String, Int>,
    radiusOuter: Dp = 90.dp,
    chartBarWidth: Dp = 5.dp,
    animDuration: Int = 1000,
) {

    val totalSum = data.values.sum()
    val floatValue = mutableListOf<Float>()

    // To set the value of each Arc according to
    // the value given in the data, we have used a simple formula.
    // For a detailed explanation check out the Medium Article.
    // The link is in the about section and readme file of this GitHub Repository
    data.values.forEachIndexed { index, values ->
        floatValue.add(index, 360 * values.toFloat() / totalSum.toFloat())
    }

    // add the colors as per the number of data(no. of pie chart entries)
    // so that each data will get a color
    val colors = listOf(
        Primary_Green, Transparent_Green
    )

    var animationPlayed = remember { mutableStateOf(false) }

    var lastValue = 0f

    // it is the diameter value of the Pie
    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed.value) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    // if you want to stabilize the Pie Chart you can use value -90f
    // 90f is used to complete 1/4 of the rotation
    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed.value) 90f * 11f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    // to play the animation only once when the function is Created or Recomposed
    LaunchedEffect(key1 = true) {
        animationPlayed.value = true
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Pie Chart using Canvas Arc
        Box(
            modifier = Modifier.size(animateSize.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
                    .rotate(animateRotation)
            ) {
                // draw each Arc for each data entry in Pie Chart
                floatValue.forEachIndexed { index, value ->
                    drawArc(
                        color = colors[index],
                        lastValue,
                        value,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                    )
                    lastValue += value
                }
            }
        }

        // To see the data in more structured way
        // Compose Function in which Items are showing data
        DetailsPieChart(
            data = data,
            colors = colors
        )

    }

}

@Composable
fun DetailsPieChart(
    data: Map<String, Int>,
    colors: List<Color>
) {
    Column(
        modifier = Modifier
            .padding(top = 80.dp)
            .fillMaxWidth()
    ) {
        // create the data items
        data.values.forEachIndexed { index, value ->
            DetailsPieChartItem(
                data = Pair(data.keys.elementAt(index), value),
                color = colors[index]
            )
        }

    }
}

@Composable
fun DetailsPieChartItem(
    data: Pair<String, Int>,
    height: Dp = 45.dp,
    color: Color
) {

    Surface(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 40.dp),
        color = Color.Transparent
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .background(
                        color = color,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .size(height)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = data.first,
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                    color = Color.Black
                )
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = data.second.toString(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                    color = Color.Gray
                )
            }

        }

    }

}

@Composable
fun CircularProgressbar2(
    number: Float = 60f,
    numberStyle: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    size: Dp = 120.dp,
    thickness: Dp = 14.dp,
    animationDuration: Int = 1000,
    animationDelay: Int = 200,
    foregroundIndicatorColor: Color = Color(0xFF35898f),
    backgroundIndicatorColor: Color = foregroundIndicatorColor.copy(alpha = 0.5f),
    extraSizeForegroundIndicator: Dp = 12.dp
) {

    // It remembers the number value
    var numberR = remember {
        mutableStateOf(-1f)
    }

    // Number Animation
    val animateNumber = animateFloatAsState(
        targetValue = numberR.value,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        )
    )

    // This is to start the animation when the activity is opened
    LaunchedEffect(Unit) {
        numberR.value = number
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size = size)
    ) {
        Canvas(
            modifier = Modifier
                .size(size = size)
        ) {

            // Background circle
            drawCircle(
                color = backgroundIndicatorColor,
                radius = size.toPx() / 2,
                style = Stroke(width = thickness.toPx(), cap = StrokeCap.Round)
            )

            val sweepAngle = (animateNumber.value / 100) * 360

            // Foreground circle
            drawArc(
                color = foregroundIndicatorColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(
                    (thickness + extraSizeForegroundIndicator).toPx(),
                    cap = StrokeCap.Butt
                )
            )
        }

        // Text that shows number inside the circle
        Text(
            text = (animateNumber.value).toInt().toString() + "%",
            style = numberStyle
        )
    }


}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PickImageFromGallery(contract: Int, admission: Int, loaded: String?) {
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri.value = uri
        }
    val disabled = remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            val launcher_ = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission Accepted: Do something
                    Log.d("ExampleScreen", "PERMISSION GRANTED")

                } else {
                    // Permission Denied: Do something
                    Log.d("ExampleScreen", "PERMISSION DENIED")
                }
            }
            val rem = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { Log.d("MyLogHETE", "TEST") }
            val cameraPermissionState = rememberPermissionState(
                android.Manifest.permission.READ_MEDIA_AUDIO
            )

            Button(
                onClick = {
                    // Camera permission state

                    val permissionCheck = ContextCompat.checkSelfPermission(
                        context,
                        CAMERA
                    )
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        launcher.launch("image/*")
                    } else {
                        rem.launch(CAMERA)
//                    cameraPermissionState.launchPermissionRequest()
                        Toast.makeText(
                            context,
                            "Разрешите приложению доступ к галерее",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary_Green),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Text(text = "Выбрать", color = Color.White)
            }

            if (imageUri.value !== null) {
                Button(
                    onClick = {
                        Log.d("MyLog", imageUri.value!!.path.toString())
                        Log.d("MyLog", imageUri.value!!.query.toString())
                        val uriPathHelper = URIPathHelper()
                        val filePath = uriPathHelper.getPath(context, imageUri.value!!)
                        val file: File = File(filePath.toString())
                        val test =
                            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                        val body = UploadRequestBody(file, "image")
                        val pref = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                        val token = pref.getString("token_lk", "")
                        UploadImage().uploadImage(
                            "Bearer " + token?.trim('"'),
                            MultipartBody.Part.createFormData(
                                "files[0]",
                                file.name, body
                            ), contract, admission
                        ).enqueue(object : retrofit2.Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.e("API Request", "I got an error and i don't know why :(")
                                Log.e("API Request", t.message.toString())
                            }

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                Log.d("API Request", response.body().toString())
                                Log.d("API Request", response.message())
                                Log.d("API Request", response.errorBody().toString())
                                Log.d("API Request", response.raw().body.toString())
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Документ отправлен на проверку",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    disabled.value = true

                                }
                            }
                        })


                    },
                    enabled = !disabled.value,
                    colors = ButtonDefaults.buttonColors(containerColor = Aggressive_red)
                ) {
                    Text("Загрузить", color = Color.White)
                }
            }
        }
        imageUri.let {
            imageUri.value = it.value
            if (it.value !== null) {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap.value = MediaStore.Images
                        .Media.getBitmap(context.contentResolver, it.value)

                } else {
                    val source = ImageDecoder
                        .createSource(context.contentResolver, it.value!!)
                    bitmap.value = ImageDecoder.decodeBitmap(source)
                }
            }


        }
        if (bitmap.value !== null) {
            Image(
                bitmap = bitmap.value!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(20.dp)
            )
        } else {
            if (loaded !== null) {
                AsyncImage(
                    model = "https://lk.mzpo-s.ru/build/images/$loaded",
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .padding(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))


    }


}


@Composable
fun LoadableScreen(
    loaded: MutableState<Boolean>,
    error: MutableState<Boolean> = mutableStateOf(false),
    page: @Composable () -> Unit = {}
) {
    if (!loaded.value) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (error.value) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "",
                modifier = Modifier.size(100.dp),
                tint = Primary_Green
            )
            Text(
                text = "Произошла ошибка, попробуйте позже",
                fontSize = 16.sp,
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = "Версия приложения: " + BuildConfig.VERSION_NAME,
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    } else {
        page.invoke()
    }
}


class URIPathHelper {

    fun getPath(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val uri1 = uri.path
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                Log.i("MyLog", "ext")

                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

            } else if (isDownloadsDocument(uri)) {
                Log.i("MyLog", "dwn")

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                Log.i("MyLog", "media $uri")

                val docId = DocumentsContract.getDocumentId(uri)

                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                Log.d("MyLog", contentUri.toString() + " " + selection + " " + selectionArgs[0])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = uri?.let {
                context.contentResolver.query(
                    it,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
            }
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}


fun getDate(string: String): LocalDate {
    val firstApiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(string, firstApiFormat)
}

@Composable
fun Privacy() {
    val url = LocalUriHandler.current
    val annotatedString = buildAnnotatedString {

        append("Отправляя заявку, вы даете согласие на обработку персональных данных в соответствии с ")


        pushStringAnnotation(tag = "terms", annotation = "https://www.mzpo-s.ru/contacts/privacy")

        withStyle(style = SpanStyle(color = Primary_Green)) {
            append("политикой конфиденциальности")
        }

        pop()
    }

    ClickableText(
        text = annotatedString,
        style = TextStyle(textAlign = TextAlign.Center),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "terms", start = offset, end = offset)
                .firstOrNull()?.let {
                url.openUri(it.item)
            }
        })
}