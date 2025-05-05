package net.leo.agendadecontactos.clases

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import java.net.NetworkInterface
import java.util.*

object Configuraciones {

    private const val URL_EMULADOR = "http://10.0.2.2/api_agenda/controllers/api_contactos.php"


    private const val URL_DISPOSITIVO = "http://192.168.1.4/api_agenda/controllers/api_contactos.php"


    fun getApiUrl(context: Context): String {

        val isEmulator = android.os.Build.PRODUCT.contains("sdk") ||
                android.os.Build.MODEL.contains("Emulator") ||
                android.os.Build.MODEL.contains("Android SDK")

        return if (isEmulator) {
            URL_EMULADOR
        } else {
            URL_DISPOSITIVO
        }
    }

    const val URL_WEBSERVICES = "http://192.168.1.4/api_agenda/controllers/api_contactos.php"
}