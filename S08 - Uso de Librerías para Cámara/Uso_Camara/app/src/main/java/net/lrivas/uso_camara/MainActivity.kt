package net.lrivas.uso_camara

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var btnTomarFoto: Button
    private lateinit var imgFoto: ImageView
    private lateinit var btnEnviarCorreo: Button
    private lateinit var btnEnviarWhatsApp: Button
    private lateinit var currentPhotoPath: String

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_PERMISSION = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTomarFoto = findViewById(R.id.buttonTakePhoto)
        imgFoto = findViewById(R.id.imageView)
        btnEnviarCorreo = findViewById(R.id.buttonSendEmail)
        btnEnviarWhatsApp = findViewById(R.id.buttonSendWhatsApp)

        btnTomarFoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION)
            } else {
                dispatchTakePictureIntent()
            }
        }

        btnEnviarCorreo.setOnClickListener {
            sendEmail()
        }

        btnEnviarWhatsApp.setOnClickListener {
            sendWhatsApp()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "net.lrivas.uso_camara.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic()
        }
    }

    private fun setPic() {
        val targetW: Int = imgFoto.width
        val targetH: Int = imgFoto.height

        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(currentPhotoPath, this)
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }

        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        imgFoto.setImageBitmap(bitmap)
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "image/jpeg"
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(currentPhotoPath))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Foto desde la app")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Aquí tienes la foto tomada.")
        startActivity(Intent.createChooser(emailIntent, "Enviar correo electrónico"))
    }

    private fun sendWhatsApp() {
        val photoFile = File(currentPhotoPath)
        val uri = FileProvider.getUriForFile(this, "net.lrivas.uso_camara.fileprovider", photoFile)
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "image/jpeg"
        shareIntent.setPackage("com.whatsapp")
        startActivity(Intent.createChooser(shareIntent, "Enviar con WhatsApp"))
    }
}