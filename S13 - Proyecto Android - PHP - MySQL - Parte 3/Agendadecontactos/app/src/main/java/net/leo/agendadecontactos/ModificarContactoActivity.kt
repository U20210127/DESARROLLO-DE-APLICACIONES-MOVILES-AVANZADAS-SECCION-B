package net.leo.agendadecontactos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import net.leo.agendadecontactos.clases.Configuraciones
import org.json.JSONObject

class ModificarContactoActivity : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextTelefono: EditText
    private var idContacto: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modificar_contacto_activity)

        // Inicializar vistas
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextTelefono = findViewById(R.id.editTextTelefono)
        val buttonGuardar = findViewById<Button>(R.id.buttonGuardar)
        val buttonEliminar = findViewById<Button>(R.id.buttonEliminar)

        // Obtener datos del intent
        idContacto = intent.getIntExtra("id_contacto", 0)
        val nombre = intent.getStringExtra("nombre") ?: ""
        val telefono = intent.getStringExtra("telefono") ?: ""

        // Establecer datos en los campos
        editTextNombre.setText(nombre)
        editTextTelefono.setText(telefono)

        // Configurar listener para el botón guardar
        buttonGuardar.setOnClickListener {
            val nuevoNombre = editTextNombre.text.toString()
            val nuevoTelefono = editTextTelefono.text.toString()

            if (nuevoNombre.isNotEmpty() && nuevoTelefono.isNotEmpty()) {
                modificarContacto(idContacto, nuevoNombre, nuevoTelefono)
            } else {
                Toast.makeText(this, "Introduce nombre y teléfono", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar listener para el botón eliminar
        buttonEliminar.setOnClickListener {
            mostrarDialogoConfirmacion()
        }
    }

    private fun modificarContacto(id: Int, nombre: String, telefono: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "${Configuraciones.getApiUrl(this)}?action=actualizarContacto"

        val jsonBody = JSONObject().apply {
            put("id_contacto", id)
            put("nombre", nombre)
            put("telefono", telefono)
        }

        val jsonRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                try {
                    val status = if (response.has("status") && !response.isNull("status")) {
                        response.getString("status")
                    } else {
                        "error"
                    }

                    if (status == "success") {
                        val message = response.optString("message", "Contacto actualizado con éxito")
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        val message = response.optString("message", "Error desconocido al actualizar contacto")
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al parsear respuesta: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val errorMsg = when (error) {
                    is com.android.volley.TimeoutError -> "Tiempo de espera agotado"
                    is com.android.volley.NoConnectionError -> "No hay conexión a Internet o al servidor"
                    is com.android.volley.AuthFailureError -> "Error de autenticación"
                    is com.android.volley.ServerError -> "Error del servidor: ${error.networkResponse?.statusCode}"
                    is com.android.volley.NetworkError -> "Error de red general"
                    is com.android.volley.ParseError -> "Error al analizar la respuesta JSON"
                    else -> error.message ?: "Desconocido"
                }
                Toast.makeText(this, "Error de conexión: $errorMsg", Toast.LENGTH_LONG).show()
            }
        )

        queue.add(jsonRequest)
    }

    private fun eliminarContacto(id: Int) {
        val queue = Volley.newRequestQueue(this)
        val url = "${Configuraciones.getApiUrl(this)}?action=eliminarContacto"

        val jsonBody = JSONObject().apply {
            put("id_contacto", id)
        }

        val jsonRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                try {
                    val status = if (response.has("status") && !response.isNull("status")) {
                        response.getString("status")
                    } else {
                        "error"
                    }

                    if (status == "success") {
                        val message = response.optString("message", "Contacto eliminado con éxito")
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        val message = response.optString("message", "Error desconocido al eliminar contacto")
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al parsear respuesta: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val errorMsg = when (error) {
                    is com.android.volley.TimeoutError -> "Tiempo de espera agotado"
                    is com.android.volley.NoConnectionError -> "No hay conexión a Internet o al servidor"
                    is com.android.volley.AuthFailureError -> "Error de autenticación"
                    is com.android.volley.ServerError -> "Error del servidor: ${error.networkResponse?.statusCode}"
                    is com.android.volley.NetworkError -> "Error de red general"
                    is com.android.volley.ParseError -> "Error al analizar la respuesta JSON"
                    else -> error.message ?: "Desconocido"
                }
                Toast.makeText(this, "Error de conexión: $errorMsg", Toast.LENGTH_LONG).show()
            }
        )

        queue.add(jsonRequest)
    }

    private fun mostrarDialogoConfirmacion() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar contacto")
            .setMessage("¿Estás seguro de que deseas eliminar este contacto?")
            .setPositiveButton("Sí") { _, _ ->
                eliminarContacto(idContacto)
            }
            .setNegativeButton("No", null)
            .show()
    }
}