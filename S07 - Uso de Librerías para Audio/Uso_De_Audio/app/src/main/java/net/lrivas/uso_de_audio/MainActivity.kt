package net.lrivas.uso_de_audio

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.languages,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            languageSpinner.adapter = adapter
        }

        val btnNumbers: Button = findViewById(R.id.btnNumbers)
        val btnColors: Button = findViewById(R.id.btnColors)
        val btnAnimals: Button = findViewById(R.id.btnAnimals)

        btnNumbers.setOnClickListener {
            val intent = Intent(this, LearningActivity::class.java)
            intent.putExtra("category", "numbers")
            intent.putExtra("language", languageSpinner.selectedItem.toString())
            startActivity(intent)
        }

        btnColors.setOnClickListener {
            val intent = Intent(this, LearningActivity::class.java)
            intent.putExtra("category", "colors")
            intent.putExtra("language", languageSpinner.selectedItem.toString())
            startActivity(intent)
        }

        btnAnimals.setOnClickListener {
            val intent = Intent(this, LearningActivity::class.java)
            intent.putExtra("category", "animals")
            intent.putExtra("language", languageSpinner.selectedItem.toString())
            startActivity(intent)
        }
    }
}