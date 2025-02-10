package net.lrivas.usodesensores

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.lrivas.usodesensores.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnProximity.setOnClickListener {
            startActivity(Intent(this, ProximityActivity::class.java))
        }

        binding.btnLuminosity.setOnClickListener {
            startActivity(Intent(this, LightActivity::class.java))
        }
    }
}
