package com.example.practica5dama

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var currentPhotoPath: String
    private var currentPhotoFile: File? = null
    private lateinit var btnCapture: Button
    private lateinit var btnShare: Button
    private lateinit var btnDelete: Button
    private lateinit var textViewMetadata: TextView
    private lateinit var imgPreview: ImageView

    private val REQUEST_CAMERA_PERMISSIONS = 100
    private val REQUEST_IMAGE_CAPTURE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar vistas
        btnCapture = findViewById(R.id.btnCapture)
        btnShare = findViewById(R.id.btnShare)
        btnDelete = findViewById(R.id.btnDelete)
        textViewMetadata = findViewById(R.id.textViewMetadata)
        imgPreview = findViewById(R.id.imgPreview)

        // Configurar eventos
        btnCapture.setOnClickListener { checkCameraPermission() }
        btnShare.setOnClickListener { sharePhoto() }
        btnDelete.setOnClickListener { showDeleteConfirmationDialog() }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSIONS)
        }
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                val photoFile = createImageFile()
                val photoURI = FileProvider.getUriForFile(this, "$packageName.fileprovider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (ex: IOException) {
                Toast.makeText(this, "Error al crear el archivo de imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
            currentPhotoFile = this
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            currentPhotoFile?.let {
                imgPreview.setImageURI(Uri.fromFile(it))
                textViewMetadata.text = "Ruta: $currentPhotoPath"
            }
        }
    }

    private fun sharePhoto() {
        if (currentPhotoFile != null) {
            val photoURI = FileProvider.getUriForFile(this, "$packageName.fileprovider", currentPhotoFile!!)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, photoURI)
                type = "image/jpeg"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir imagen"))
        } else {
            Toast.makeText(this, "No hay foto para compartir", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para mostrar la alerta de confirmación antes de eliminar la foto
    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que deseas eliminar la foto?")
        builder.setPositiveButton("Sí") { dialog, which ->
            deletePhoto()
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }

        // Mostrar el diálogo
        builder.show()
    }

    private fun deletePhoto() {
        currentPhotoFile?.let {
            if (it.exists()) {
                it.delete()
                imgPreview.setImageBitmap(null)
                textViewMetadata.text = "Foto eliminada"
                currentPhotoFile = null
                Toast.makeText(this, "Foto eliminada", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "No hay foto para eliminar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
