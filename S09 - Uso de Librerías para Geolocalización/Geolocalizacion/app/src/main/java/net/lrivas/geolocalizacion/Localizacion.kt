package net.lrivas.geolocalizacion

import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationProvider
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class Localizacion(
    private val textoLatitud: TextView,
    private val textoLongitud: TextView,
    private val textoDireccion: TextView,
    private val mainActivity: MainActivity
) : LocationListener {

    private var lastLocation: Location? = null
    private val geocoder: Geocoder by lazy { Geocoder(mainActivity, Locale.getDefault()) }

    fun setLastLocation(location: Location) {
        lastLocation = location
        updateAddress(location)
    }

    override fun onLocationChanged(location: Location) {
        textoLatitud.text = String.format("%.6f", location.latitude)
        textoLongitud.text = String.format("%.6f", location.longitude)
        updateAddress(location)
        lastLocation = location
    }

    private fun updateAddress(location: Location) {
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1) ?: emptyList()
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val addressText = address.getAddressLine(0) ?: "Sin dirección"
                mainActivity.runOnUiThread {
                    textoDireccion.text = addressText
                }
            } else {
                mainActivity.runOnUiThread {
                    textoDireccion.text = "Sin dirección"
                }
            }
        } catch (e: Exception) {
            mainActivity.runOnUiThread {
                textoDireccion.text = "Error al obtener dirección"
            }
        }
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText(mainActivity, "GPS Desactivado", Toast.LENGTH_SHORT).show()
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText(mainActivity, "GPS Activado", Toast.LENGTH_SHORT).show()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        when (status) {
            LocationProvider.AVAILABLE -> {}
            LocationProvider.OUT_OF_SERVICE -> {}
            LocationProvider.TEMPORARILY_UNAVAILABLE -> {}
        }
    }
}