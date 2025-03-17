package lk.mzpo.ru.services

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession

class CustomTabHelper(private val context: Context,
                      public var onClosed: () -> Unit) {

    private var customTabsClient: CustomTabsClient? = null
    private var customTabsSession: CustomTabsSession? = null
    private var bound = false
    private var wasOpened = false // Флаг для отслеживания возврата

    private val connection = object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
            customTabsClient = client
            customTabsSession = client.newSession(null)
            bound = true
            Log.d("CustomTabHelper", "CustomTabsService connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            customTabsClient = null
            bound = false
            Log.d("CustomTabHelper", "CustomTabsService disconnected")
            onClosed() // Запускаем проверку платежа при разрыве соединения
        }
    }

    fun openUrl(url: String) {
        wasOpened = true

        val intent = CustomTabsIntent.Builder().build()
        intent.intent.setPackage("com.android.chrome") // Указываем Chrome
        val uri = Uri.parse(url)

        intent.launchUrl(context, uri) // Открываем URL

        // Отслеживаем, когда приложение вернется после редиректа
        Handler(Looper.getMainLooper()).postDelayed({
            if (wasOpened) {
                wasOpened = false
                onClosed() // Закрываем CustomTabsIntent
            }
            Log.d("MyCustomLog", "Testing")
        }, 5000) // Проверяем через 5 секунд
    }



    fun unbindService() {
        if (bound) {
            Log.d("CustomTabHelper", "Unbinding CustomTabsService")
            context.unbindService(connection)
            bound = false
        }
    }

    fun handleReturnToCart() {
        if (wasOpened) {
            wasOpened = false
            onClosed()
        }
        unbindService() // Удаляем соединение при возврате
    }
}
