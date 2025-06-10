package com.example.practica3android

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private var currentBitmap: Bitmap? = null
    private var originalBitmap: Bitmap? = null // Guarda la imagen original
    private var isGrayscale = false // Estado del filtro
    private var isZoomed = false // Estado del zoom
    private var cropLevel = 0 // Nivel de recorte (0 = sin recortar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        val btnSelectImage: Button = findViewById(R.id.btnSelectImage)
        val btnRotate: Button = findViewById(R.id.btnRotate)
        val btnZoom: Button = findViewById(R.id.btnZoom)
        val btnMirror: Button = findViewById(R.id.btnMirror)
        val btnGrayscale: Button = findViewById(R.id.btnGrayscale)
        val btnCrop: Button = findViewById(R.id.btnCrop)
        val btnDownload: Button = findViewById(R.id.btnDownload)

        // Cargar imagen inicial
        currentBitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_image)
        originalBitmap = currentBitmap?.copy(Bitmap.Config.ARGB_8888, true) // Copia original
        imageView.setImageBitmap(currentBitmap)

        btnSelectImage.setOnClickListener { pickImageFromGallery() }
        btnRotate.setOnClickListener { rotateImage() }
        btnZoom.setOnClickListener { toggleZoom() }
        btnMirror.setOnClickListener { mirrorImage() }
        btnGrayscale.setOnClickListener { applyGrayscale() }
        btnCrop.setOnClickListener { toggleCrop() }
        btnDownload.setOnClickListener { downloadImage() }
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    loadBitmapFromUri(uri)
                }
            }
        }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun loadBitmapFromUri(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            currentBitmap = bitmap
            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true) // Copia original
            cropLevel = 0 // Reiniciar recorte
            isZoomed = false // Reiniciar zoom
            imageView.scaleX = 1f
            imageView.scaleY = 1f
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun rotateImage() {
        currentBitmap?.let { bitmap ->
            val matrix = Matrix()
            matrix.postRotate(90f)
            val rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            imageView.setImageBitmap(rotatedBitmap)
            currentBitmap = rotatedBitmap
        }
    }

    private fun toggleZoom() {
        if (isZoomed) {
            // Restaurar tamaño normal
            imageView.scaleX = 1f
            imageView.scaleY = 1f
        } else {
            // Aplicar zoom (máximo 2x)
            imageView.scaleX = 2f
            imageView.scaleY = 2f
        }
        isZoomed = !isZoomed // Alternar estado
    }

    private fun mirrorImage() {
        currentBitmap?.let { bitmap ->
            val matrix = Matrix()
            matrix.preScale(-1f, 1f)
            val mirroredBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            imageView.setImageBitmap(mirroredBitmap)
            currentBitmap = mirroredBitmap
        }
    }

    private fun applyGrayscale() {
        currentBitmap?.let { bitmap ->
            if (!isGrayscale) {
                val grayBitmap =
                    Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(grayBitmap)
                val paint = Paint()
                val colorMatrix = ColorMatrix()
                colorMatrix.setSaturation(0f)
                val filter = ColorMatrixColorFilter(colorMatrix)
                paint.colorFilter = filter
                canvas.drawBitmap(bitmap, 0f, 0f, paint)

                imageView.setImageBitmap(grayBitmap)
                currentBitmap = grayBitmap
            } else {
                originalBitmap?.let { original ->
                    imageView.setImageBitmap(original)
                    currentBitmap = original
                }
            }

            isGrayscale = !isGrayscale
        }
    }

    private fun toggleCrop() {
        if (cropLevel < 2) {
            cropLevel++
        } else {
            cropLevel = 0
        }

        if (cropLevel == 0) {
            originalBitmap?.let {
                imageView.setImageBitmap(it)
                currentBitmap = it
            }
        } else {
            currentBitmap?.let { bitmap ->
                val width = bitmap.width
                val height = bitmap.height
                val factor = if (cropLevel == 1) 0.8 else 0.5 // Primer recorte 80%, segundo 50%

                val cropWidth = (width * factor).toInt()
                val cropHeight = (height * factor).toInt()

                val startX = (width - cropWidth) / 2
                val startY = (height - cropHeight) / 2
                val croppedBitmap =
                    Bitmap.createBitmap(bitmap, startX, startY, cropWidth, cropHeight)

                imageView.setImageBitmap(croppedBitmap)
                currentBitmap = croppedBitmap
            }
        }
    }

    private fun downloadImage() {
        currentBitmap?.let { bitmap ->
            try {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "imagen_editada.png")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
                }

                val uri =
                    contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    val outputStream: OutputStream? = contentResolver.openOutputStream(it)
                    outputStream?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        Toast.makeText(this, "Imagen guardada en la galería", Toast.LENGTH_SHORT)
                            .show()
                    }
                } ?: run {
                    Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
