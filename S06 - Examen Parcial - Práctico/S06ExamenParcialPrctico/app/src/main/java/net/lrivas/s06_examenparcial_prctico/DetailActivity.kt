package net.lrivas.s06_examenparcial_prctico

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class DetailActivity : AppCompatActivity() {

    private lateinit var doctorList: ArrayList<Doctor>
    private var doctorId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Get doctor ID from intent
        doctorId = intent.getIntExtra("DOCTOR_ID", -1)
        if (doctorId == -1) {
            finish() // Close the activity if doctor ID is not valid
            return
        }

        // Prepare data
        prepareData()

        // Find doctor by ID
        val doctor = doctorList.find { it.id == doctorId }

        if (doctor == null) {
            finish() // Close the activity if doctor is not found
            return
        }

        // Initialize views
        val backButton: ImageButton = findViewById(R.id.backButton)
        val doctorImage: ImageView = findViewById(R.id.detailDoctorImage)
        val doctorName: TextView = findViewById(R.id.detailDoctorName)
        val doctorSpecialty: TextView = findViewById(R.id.detailDoctorSpecialty)
        val doctorAbout: TextView = findViewById(R.id.detailDoctorAbout)
        val doctorEmail: TextView = findViewById(R.id.detailDoctorEmail)
        val doctorPhone: TextView = findViewById(R.id.detailDoctorPhone)
        val callButton: Button = findViewById(R.id.callButton)
        val whatsappButton: Button = findViewById(R.id.whatsappButton)

        // Set doctor data
        doctorImage.setImageResource(doctor.imageResId)
        doctorName.text = doctor.name
        doctorSpecialty.text = doctor.specialty
        doctorAbout.text = doctor.about
        doctorEmail.text = doctor.email
        doctorPhone.text = doctor.phone

        // Back button click listener
        backButton.setOnClickListener {
            finish()
        }

        // Call button click listener
        callButton.setOnClickListener {
            val phoneNumber = doctor.phone.replace("\\s".toRegex(), "")
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:$phoneNumber")
            startActivity(callIntent)
        }

        // WhatsApp button click listener
        whatsappButton.setOnClickListener {
            val phoneNumber = doctor.phone.replace("\\s".toRegex(), "")
            val whatsappIntent = Intent(Intent.ACTION_VIEW)
            whatsappIntent.data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber")
            startActivity(whatsappIntent)
        }
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