package net.lrivas.usodesensores

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.lrivas.usodesensores.databinding.ActivityLightBinding

class LightActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityLightBinding
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private var currentLux: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivityLightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sensor Manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        // Configurar el botón para enviar mensaje a WhatsApp
        binding.btnSend.setOnClickListener {
            sendWhatsAppMessage()
        }
    }

    override fun onResume() {
        super.onResume()
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_LIGHT) {
                currentLux = it.values[0]
                binding.tvValueLight.text = "VALOR: $currentLux LUX"
            }
        }
    }

    private fun sendWhatsAppMessage() {
        val message = "Valor actual del sensor: $currentLux LUX"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            setPackage("com.whatsapp")
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}