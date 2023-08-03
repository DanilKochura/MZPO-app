package lk.mzpo.ru.ui.components

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.Primary_Green
import java.lang.Error
import kotlin.math.log

@Composable
fun RedButton(onClick: () -> Unit, unit: @Composable () -> Unit)
{
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = Aggressive_red)) {
        Unit
    }
}

@Composable
fun EmailTextField(email: MutableState<TextFieldValue>, isError: MutableState<Boolean>)
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
        modifier = Modifier.padding(top = 10.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Primary_Green,  cursorColor = Color.Black),
        isError = isError.value,
        singleLine = true
    )
}

@Composable
fun PhoneTextField(phone: MutableState<String>, isError: MutableState<Boolean>)
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
        modifier = Modifier.padding(top = 10.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Primary_Green,  cursorColor = Color.Black),
        visualTransformation =  PhoneVisualTransformation(mask, maskNumber),
        isError = isError.value,
        singleLine = true
    )
}


@Composable
fun NameTextField(name: MutableState<TextFieldValue>, isError: MutableState<Boolean> = mutableStateOf(false))
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
        modifier = Modifier.padding(top = 10.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Primary_Green,  cursorColor = Color.Black),
        isError = isError.value,
        singleLine = true
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