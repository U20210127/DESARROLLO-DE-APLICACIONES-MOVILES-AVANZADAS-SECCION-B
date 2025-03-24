package net.lrivas.geolocalizacion

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var botonObtenerCoordenadas: Button
    private lateinit var botonCompartirUbicacion: Button
    private lateinit var botonAbrirMapa: Button
    private lateinit var textoLatitud: TextView
    private lateinit var textoLongitud: TextView
    private lateinit var textoDireccion: TextView
    private lateinit var gestorUbicacion: LocationManager
    private lateinit var localizacion: Localizacion
    private var rastreandoUbicacion = false

    private val lanzadorSolicitudPermiso = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            obtenerUltimaUbicacion()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        botonObtenerCoordenadas = findViewById(R.id.btnGetCoordinates)
        botonCompartirUbicacion = findViewById(R.id.btnShareLocation)
        botonAbrirMapa = findViewById(R.id.btnOpenMap)
        textoLatitud = findViewById(R.id.tvLatitude)
        textoLongitud = findViewById(R.id.tvLongitude)
        textoDireccion = findViewById(R.id.tvAddress)
        gestorUbicacion = getSystemService(LOCATION_SERVICE) as LocationManager
        localizacion = Localizacion(textoLatitud, textoLongitud, textoDireccion, this)

        botonObtenerCoordenadas.setOnClickListener {
            if (!rastreandoUbicacion) {
                verificarPermisoUbicacion()
                botonObtenerCoordenadas.text = "Detener rastreo"
                rastreandoUbicacion = true
            } else {
                detenerRastreo()
                botonObtenerCoordenadas.text = "Obtener Coordenadas"
                rastreandoUbicacion = false
            }
        }

        botonCompartirUbicacion.setOnClickListener {
            compartirUbicacion()
        }

        botonAbrirMapa.setOnClickListener {
            abrirMapa()
        }
    }

    private fun verificarPermisoUbicacion() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                obtenerUltimaUbicacion()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(this, "Se necesita permiso de ubicación para continuar", Toast.LENGTH_LONG).show()
                lanzadorSolicitudPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                lanzadorSolicitudPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun obtenerUltimaUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val ultimaUbicacion = gestorUbicacion.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: gestorUbicacion.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (ultimaUbicacion != null) {
                textoLatitud.text = String.format("%.6f", ultimaUbicacion.latitude)
                textoLongitud.text = String.format("%.6f", ultimaUbicacion.longitude)
                localizacion.setLastLocation(ultimaUbicacion)
            } else {
                textoLatitud.text = "No disponible"
                textoLongitud.text = "No disponible"
                Toast.makeText(this, "No se encontró ubicación reciente", Toast.LENGTH_SHORT).show()
            }
            gestorUbicacion.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, localizacion)
            if (gestorUbicacion.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                gestorUbicacion.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, localizacion)
            }
            Toast.makeText(this, "Rastreo de ubicación iniciado", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            textoLatitud.text = "Error"
            textoLongitud.text = "Error"
            Toast.makeText(this, "Error de seguridad: ${e.message}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            textoLatitud.text = "Error"
            textoLongitud.text = "Error"
            Toast.makeText(this, "Error al obtener ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun detenerRastreo() {
        gestorUbicacion.removeUpdates(localizacion)
        Toast.makeText(this, "Rastreo de ubicación detenido", Toast.LENGTH_SHORT).show()
    }

    private fun compartirUbicacion() {
        val latitud = textoLatitud.text.toString()
        val longitud = textoLongitud.text.toString()
        val mensaje = "Hola, te adjunto mi ubicación: https://maps.google.com/?q=$latitud,$longitud"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, mensaje)
        intent.setPackage("com.whatsapp")
        startActivity(Intent.createChooser(intent, "Compartir ubicación via"))
    }

    private fun abrirMapa() {
        val latitud = textoLatitud.text.toString().toDouble()
        val longitud = textoLongitud.text.toString().toDouble()
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("LATITUD", latitud)
        intent.putExtra("LONGITUD", longitud)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        if (rastreandoUbicacion) {
            detenerRastreo()
            botonObtenerCoordenadas.text = "Obtener Coordenadas"
            rastreandoUbicacion = false
        }
    }
}