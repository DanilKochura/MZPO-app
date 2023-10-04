package lk.mzpo.ru.ui.components

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.CalendarView
import android.widget.DatePicker
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import lk.mzpo.ru.R
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.Primary_Green
import java.lang.Error
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.log
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Bundle
import android.service.autofill.DateTransformation
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*
@Composable
fun RedButton(onClick: () -> Unit, unit: @Composable () -> Unit)
{
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = Aggressive_red)) {
        Unit
    }
}

@Composable
fun EmailTextField(email: MutableState<TextFieldValue>, isError: MutableState<Boolean>, modifier: Modifier = Modifier)
{
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
        singleLine = true
    )
}
@Composable
fun PasswordTextField(password: MutableState<TextFieldValue>, isError: MutableState<Boolean>, modifier: Modifier = Modifier)
{
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
        visualTransformation = if(!pwtr.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = {
               pwtr.value = !pwtr.value
            }) {
                Icon(painter = painterResource(id = R.drawable.baseline_visibility_24), contentDescription = "password_eye")
            }
        }
    )
}
@Composable
fun PriceTextField(price: MutableState<String>, placeholder: String, label: String, modifier: Modifier = Modifier)
{
    OutlinedTextField(
        value = price.value,
        placeholder = { Text(text = placeholder, Modifier.alpha(0.5f)) },
        onValueChange = { price.value = it },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(text = label) },
        modifier = modifier.padding(top = 10.dp),
        colors = customTextFieldBorderColor(),
        singleLine = true
    )
}

@Composable
fun PhoneTextField(phone: MutableState<String>, isError: MutableState<Boolean>, modifier: Modifier = Modifier, readonly: Boolean = false)
{
    val mask =  "+7(000)-000-00-00"
    val maskNumber = '0'
    OutlinedTextField(
        value = phone.value,
        placeholder = { Text(text = "+7-988-281-14-07", Modifier.alpha(0.5f))},
        onValueChange = { phone.value = it.take(mask.count { it == maskNumber }) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Email Icon"
            )
        },
        label = { Text(text = "Телефон")},
        modifier = modifier.padding(top = 10.dp),
        colors = customTextFieldBorderColor(),
        visualTransformation =  PhoneVisualTransformation(mask, maskNumber),
        isError = isError.value,
        singleLine = true,
        readOnly = readonly
    )
}


@Composable
fun NameTextField(name: MutableState<TextFieldValue>, isError: MutableState<Boolean> = mutableStateOf(false), modifier: Modifier = Modifier, readonly: Boolean = false)
{
    OutlinedTextField(
        value = name.value,
        placeholder = { Text(text = "Иванов Иван Иванович", Modifier.alpha(0.5f))},
        onValueChange = { name.value = it },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Email Icon"
            )
        },
        label = { Text(text = "ФИО")},
        modifier = modifier.padding(top = 10.dp),
        colors = customTextFieldBorderColor(),
        isError = isError.value,
        singleLine = true,
         readOnly = readonly,
        enabled = !readonly,
    )
}

@Composable
fun CustomTextField(name: String, placeholder: String, icon: ImageVector? = null, value: MutableState<TextFieldValue>, isError: MutableState<Boolean> = mutableStateOf(false), modifier: Modifier = Modifier, readonly: Boolean = false)
{
    OutlinedTextField(
        value = value.value,
        placeholder = { Text(text = placeholder, Modifier.alpha(0.5f))},
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
        label = { Text(text = name)},
        modifier = modifier.padding(top = 10.dp),
        colors = customTextFieldBorderColor(),
        isError = isError.value,
        singleLine = true,
        readOnly = readonly,
        enabled = !readonly,
    )
}
@Composable
fun DateTextField(name: String, icon: ImageVector? = null, value: MutableState<TextFieldValue>, isError: MutableState<Boolean> = mutableStateOf(false), modifier: Modifier = Modifier, readonly: Boolean = false)
{
    OutlinedTextField(
        value = value.value,
        placeholder = { Text(text = "2020-02-10", Modifier.alpha(0.5f))},
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
        label = { Text(text = name)},
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
            if (offset <= 5) return offset +1
            if (offset <= 8) return offset +2
            return 10
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <=5) return offset
            if (offset <=8) return offset -1
            if (offset <=10) return offset -2
            return 8
        }
    }

    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
}

@Composable
fun customTextFieldBorderColor(): TextFieldColors
{
    return TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Primary_Green,  cursorColor = Color.Black, unfocusedBorderColor = Color.LightGray, disabledBorderColor = Color.Gray
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
    if(phone.startsWith("+7"))
    {
        return phone.substring(2)
    } else if(phone.startsWith("8"))
    {
        return phone.trimStart('8')
    }
    else return phone
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
    chartBarWidth: Dp = 10.dp,
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
        Primary_Green, Color.LightGray
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