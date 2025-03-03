package net.lrivas.s06_examenparcial_prctico

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var doctorAdapter: DoctorAdapter
    private lateinit var doctorList: ArrayList<Doctor>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Prepare data
        prepareData()

        // Initialize adapter
        doctorAdapter = DoctorAdapter(doctorList) { doctor ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("DOCTOR_ID", doctor.id)
            startActivity(intent)
        }

        recyclerView.adapter = doctorAdapter
    }

    private fun prepareData() {
        doctorList = ArrayList()

        // Add 5 doctors to the list
        doctorList.add(
            Doctor(
                1,
                "Dr. Juan Pérez",
                "Cardiología",
                "7 Años",
                "2.5k",
                R.drawable.doctor1,
                "Especialista en enfermedades cardiovasculares...",
                "juan.perez@medical.com",
                "+503 7777 4552",
                R.color.blue_card
            )
        )

        doctorList.add(
            Doctor(
                2,
                "Dr. Carlos López",
                "Dermatología",
                "6 Años",
                "2.0k",
                R.drawable.doctor2,
                "Dermatólogo certificado especializado en tratamientos láser...",
                "carlos.lopez@medical.com",
                "+503 7777 4553",
                R.color.teal_card
            )
        )

        doctorList.add(
            Doctor(
                3,
                "Dr. Luis Martínez",
                "Neurología",
                "9 Años",
                "3.8k",
                R.drawable.doctor3,
                "Neurólogo especializado en trastornos del movimiento...",
                "luis.martinez@medical.com",
                "+503 7777 4554",
                R.color.orange_card
            )
        )

        doctorList.add(
            Doctor(
                4,
                "Dr. Miguel Hernández",
                "Pediatría",
                "8 Años",
                "4.5k",
                R.drawable.doctor4,
                "Pediatra con especial interés en enfermedades infecciosas...",
                "miguel.hernandez@medical.com",
                "+503 7777 4555",
                R.color.pink_card
            )
        )

        doctorList.add(
            Doctor(
                5,
                "Dr. Roberto García",
                "Oftalmología",
                "5 Años",
                "1.8k",
                R.drawable.doctor5,
                "Oftalmólogo especializado en cirugía de cataratas...",
                "roberto.garcia@medical.com",
                "+503 7777 4556",
                R.color.green_card
            )
        )
    }
}
