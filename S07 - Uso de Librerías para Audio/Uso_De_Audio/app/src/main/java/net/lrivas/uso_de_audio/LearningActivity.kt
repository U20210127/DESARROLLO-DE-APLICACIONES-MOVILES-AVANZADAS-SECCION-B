package net.lrivas.uso_de_audio

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LearningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)

        val category = intent.getStringExtra("category")
        val language = intent.getStringExtra("language")
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)

        val (elements, namesEnglish, namesSpanish) = when (category) {
            "numbers" -> Triple(
                if (language == "English") {
                    listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten")
                } else {
                    listOf("uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez")
                },
                listOf("One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"),
                listOf("Uno", "Dos", "Tres", "Cuatro", "Cinco", "Seis", "Siete", "Ocho", "Nueve", "Diez")
            )
            "colors" -> Triple(
                if (language == "English") {
                    listOf("red", "blue", "green", "yellow", "orange", "purple", "brown", "black", "white", "gray")
                } else {
                    listOf("rojo", "azul", "verde", "amarillo", "naranja", "morado", "marron", "negro", "blanco", "gris")
                },
                listOf("Red", "Blue", "Green", "Yellow", "Orange", "Purple", "Brown", "Black", "White", "Gray"),
                listOf("Rojo", "Azul", "Verde", "Amarillo", "Naranja", "Morado", "Marron", "Negro", "Blanco", "Gris")
            )
            "animals" -> Triple(
                if (language == "English") {
                    listOf("dog", "cat", "bird", "fish", "cow", "horse", "elephant", "lion", "tiger", "bear")
                } else {
                    listOf("perro", "gato", "pajaro", "pez", "vaca", "caballo", "elefante", "leon", "tigre", "oso")
                },
                listOf("Dog", "Cat", "Bird", "Fish", "Cow", "Horse", "Elephant", "Lion", "Tiger", "Bear"),
                listOf("Perro", "Gato", "Pájaro", "Pez", "Vaca", "Caballo", "Elefante", "León", "Tigre", "Oso")
            )
            else -> Triple(emptyList(), emptyList(), emptyList())
        }

        for (i in elements.indices) {
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ).apply {
                width = 200 // Ajusta el ancho según sea necesario
                height = 400 // Ajusta la altura según sea necesario
            }

            val imageView = ImageView(this)
            imageView.setImageResource(getResourceId(elements[i]))
            imageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                350 // Ajusta la altura según sea necesario
            )
            imageView.setOnClickListener {
                val mediaPlayer = MediaPlayer.create(this, getSoundId(elements[i]))
                mediaPlayer?.start()
            }
            linearLayout.addView(imageView)

            val textView = TextView(this)
            textView.text = if (language == "English") {
                namesEnglish[i]
            } else {
                namesSpanish[i]
            }
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayout.addView(textView)

            gridLayout.addView(linearLayout)
        }
    }

    private fun getResourceId(element: String): Int {
        return resources.getIdentifier(element, "drawable", packageName)
    }

    private fun getSoundId(element: String): Int {
        return resources.getIdentifier(element, "raw", packageName)
    }
}