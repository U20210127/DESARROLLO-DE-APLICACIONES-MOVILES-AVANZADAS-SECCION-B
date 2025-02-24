package net.lrivas.uso_de_libreria

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        // Lista de IDs de recursos de imágenes locales
        val imageResIds = listOf(
            R.drawable.imagen1,
            R.drawable.imagen2,
            R.drawable.imagen3,
            R.drawable.imagen4,
            R.drawable.imagen5,
            R.drawable.imagen6
        )

        // Configurar el adaptador del ViewPager2
        viewPager.adapter = ImageSliderAdapter(imageResIds)

        // Configurar los indicadores del TabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Personalizar indicadores si es necesario
        }.attach()

        // Configurar el deslizamiento automático
        startAutoSlider()

        // Configurar el botón para abrir el navegador
        val btnOpenWeb: Button = findViewById(R.id.btnOpenWeb)
        btnOpenWeb.setOnClickListener {
            // URL oficial de la película o serie
            val url = "https://www.netflix.com/sv/title/80057281"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun startAutoSlider() {
        val handler = Handler(Looper.getMainLooper())
        val update = Runnable {
            val currentItem = viewPager.currentItem
            val nextItem = if (currentItem < (viewPager.adapter as ImageSliderAdapter).itemCount - 1) currentItem + 1 else 0
            viewPager.setCurrentItem(nextItem, true)
        }
        val swipeInterval = 3000L // Tiempo entre deslizamientos en milisegundos
        val swipeRunnable = object : Runnable {
            override fun run() {
                handler.postDelayed(update, swipeInterval)
            }
        }
        handler.postDelayed(swipeRunnable, swipeInterval)
    }
}