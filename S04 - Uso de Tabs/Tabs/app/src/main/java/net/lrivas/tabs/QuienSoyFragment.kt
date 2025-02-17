package net.lrivas.tabs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class QuienSoyFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_quien_soy, container, false)
        view.findViewById<TextView>(R.id.tvNombre).text = "Nombre: Mauricio Moises"
        view.findViewById<TextView>(R.id.tvApellido).text = "Apellido: Carranza Viera"
        view.findViewById<TextView>(R.id.tvCarnet).text = "Carnet: U20210127"
        view.findViewById<TextView>(R.id.tvTelefono).text = "Teléfono: 7296-6938"
        view.findViewById<Button>(R.id.btnEscribeme).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://wa.me/tu_numero_de_celular")
            startActivity(intent)
        }
        return view
    }
}