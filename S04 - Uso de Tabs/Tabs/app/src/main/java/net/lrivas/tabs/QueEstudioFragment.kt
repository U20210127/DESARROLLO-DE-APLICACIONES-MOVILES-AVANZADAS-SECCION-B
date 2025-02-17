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

class QueEstudioFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_que_estudio, container, false)
        view.findViewById<TextView>(R.id.tvCarrera).text = "Carrera: Ingeniería en Desarrollo de Software"
        view.findViewById<TextView>(R.id.tvCiclos).text = "Ciclos cursados: 5"
        view.findViewById<TextView>(R.id.tvMaterias).text = "Materias aprobadas: 8"
        view.findViewById<Button>(R.id.btnMiUniversidad).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.univo.edu.gt")
            startActivity(intent)
        }
        return view
    }
}