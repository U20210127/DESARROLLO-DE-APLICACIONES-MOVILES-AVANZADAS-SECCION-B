package net.lrivas.usodesensores

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.lrivas.usodesensores.databinding.ActivityProximityBinding

class ProximityActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityProximityBinding
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private var originalBgColor: Int = Color.WHITE
    private var originalTextBgColor: Int = Color.TRANSPARENT
    private var originalTextColor: Int = Color.BLACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivityProximityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sensor Manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        // Guardar colores originales
        originalBgColor = Color.WHITE // Puedes cambiarlo según tu diseño
        originalTextBgColor = Color.TRANSPARENT
        originalTextColor = Color.BLACK
    }

    override fun onResume() {
        super.onResume()
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_PROXIMITY) {
                val value = it.values[0]
                binding.tvValueProximity.text = "VALOR: $value"

                if (value < proximitySensor?.maximumRange ?: 5f) {
                    binding.proximityLayout.setBackgroundColor(Color.RED)
                    binding.tvValueProximity.setBackgroundColor(Color.BLUE)
                    binding.tvValueProximity.setTextColor(Color.WHITE)
                } else {
                    binding.proximityLayout.setBackgroundColor(originalBgColor)
                    binding.tvValueProximity.setBackgroundColor(originalTextBgColor)
                    binding.tvValueProximity.setTextColor(originalTextColor)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}