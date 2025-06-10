package com.example.practica7android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(mensajeRemoto: RemoteMessage) {
        super.onMessageReceived(mensajeRemoto)

        // Si el mensaje tiene una notificación
        mensajeRemoto.notification?.let {
            Log.d("FCM", "Mensaje recibido: Título=${it.title}, Cuerpo=${it.body}")
            mostrarNotificacion(titulo = it.title ?: "Título", cuerpo = it.body ?: "Cuerpo")
        }

        // Si el mensaje tiene datos adicionales
        if (mensajeRemoto.data.isNotEmpty()) {
            val tipo = mensajeRemoto.data["tipo"]
            val mensaje = mensajeRemoto.data["mensaje"]
            Log.d("FCM", "Datos recibidos: tipo=$tipo, mensaje=$mensaje")
        }
    }

    /**
     * Este método se llama cuando el token de FCM se actualiza.
     * Es necesario para manejar la actualización del token, como cuando el token
     * anterior ha sido comprometido o se genera un nuevo token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token: $token")

        // Si deseas enviar este token a tu servidor para gestionar las suscripciones,
        // o cualquier otro uso, puedes hacerlo en este lugar.
        // Ejemplo: enviar el token a tu servidor
        sendRegistrationToServer(token)
    }

    // Función para enviar el token al servidor (puedes personalizar esto)
    private fun sendRegistrationToServer(token: String) {
        // Aquí iría tu lógica para enviar el token a tu servidor
        // Por ejemplo, si estás usando Retrofit o cualquier otro método de comunicación con el servidor.
        Log.d("FCM", "Token enviado al servidor: $token")
    }

    private fun mostrarNotificacion(titulo: String, cuerpo: String) {
        val idCanal = "canal_predeterminado"
        val idNotificacion = 1

        val administradorNotificaciones =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear el canal de notificación en Android 8.0 o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                idCanal,
                "Canal Predeterminado",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // Se asigna la prioridad y la configuración del canal
            canal.description = "Notificaciones predeterminadas"
            administradorNotificaciones.createNotificationChannel(canal)
        }

        // Construir la notificación
        val notificacion = NotificationCompat.Builder(this, idCanal)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // La notificación se cierra al hacer click
            .build()

        administradorNotificaciones.notify(idNotificacion, notificacion)
    }
}

