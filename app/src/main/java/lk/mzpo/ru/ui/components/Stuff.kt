package lk.mzpo.ru.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import lk.mzpo.ru.ui.theme.Aggressive_red

@Composable
fun RedButton(onClick: () -> Unit, unit: @Composable () -> Unit)
{
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = Aggressive_red)) {
        Unit
    }
}