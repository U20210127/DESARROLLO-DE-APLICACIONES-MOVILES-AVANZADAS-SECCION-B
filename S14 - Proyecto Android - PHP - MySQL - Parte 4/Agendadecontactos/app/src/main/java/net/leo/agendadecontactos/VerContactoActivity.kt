package net.leo.agendadecontactos

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class VerContactoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_contacto)

        val textViewNombre = findViewById<TextView>(R.id.textViewNombre)
        val textViewTelefono = findViewById<TextView>(R.id.textViewTelefono)

        val nombre = intent.getStringExtra("nombre")
        val telefono = intent.getStringExtra("telefono")

        textViewNombre.text = "Nombre: $nombre"
        textViewTelefono.text = "Teléfono: $telefono"
    }

}
