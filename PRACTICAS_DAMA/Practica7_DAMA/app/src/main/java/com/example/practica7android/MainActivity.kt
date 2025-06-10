package com.example.practica7android

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var tokenTextView: TextView
    private lateinit var generateTokenButton: Button
    private lateinit var copyTokenButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        tokenTextView = findViewById(R.id.tokenTextView)
        generateTokenButton = findViewById(R.id.generateTokenButton)
        copyTokenButton = findViewById(R.id.copyTokenButton)

        // Obtener el token de Firebase cuando se presiona el botón "Generar Token"
        generateTokenButton.setOnClickListener {
            obtenerTokenFirebase()
        }

        // Copiar el token al portapapeles cuando se presiona el botón "Copiar Token"
        copyTokenButton.setOnClickListener {
            copiarTokenAlPortapapeles()
        }

        // Obtener el token de Firebase al iniciar la app
        obtenerTokenFirebase()
    }

    // Función para obtener el token de Firebase
    private fun obtenerTokenFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Obtener el token
            val token = task.result

            // Mostrar el token en el TextView (sin el prefijo "Token: ")
            tokenTextView.text = token // Actualiza el TextView con solo el token
            Log.d("FCM", token) // Mostramos el token en Logcat
            Toast.makeText(baseContext, "Token generado", Toast.LENGTH_SHORT).show() // Muestra un mensaje de Toast

            // Suscribirse al topic "server-notifications"
            suscribirseAlTopic()
        }
    }

    // Función para suscribirse al topic "server-notifications"
    private fun suscribirseAlTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("server-notifications")
            .addOnCompleteListener { task ->
                var mensaje = "Suscripción al topic 'server-notifications' exitosa."
                if (!task.isSuccessful) {
                    mensaje = "Error al suscribirse al topic 'server-notifications'."
                }
                Log.d("FCM", mensaje) // Mostrar en Logcat
                Toast.makeText(baseContext, mensaje, Toast.LENGTH_SHORT).show() // Mostrar en Toast
            }
    }

    // Función para copiar el token al portapapeles
    private fun copiarTokenAlPortapapeles() {
        val token = tokenTextView.text.toString()

        if (token.isNotEmpty()) {
            val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("FCM Token", token)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "Token copiado al portapapeles", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No hay token disponible para copiar", Toast.LENGTH_SHORT).show()
        }
    }
}




