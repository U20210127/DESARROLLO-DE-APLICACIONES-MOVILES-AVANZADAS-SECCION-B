package com.example.incidentes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a las vistas
        TextView textTipo = findViewById(R.id.textTipoDetail);
        TextView textDescripcion = findViewById(R.id.textDescripcionDetail);
        TextView textFecha = findViewById(R.id.textFechaDetail);
        TextView textEstatus = findViewById(R.id.textEstatusDetail);
        TextView textResolucion = findViewById(R.id.textResolucionDetail);
        ImageView imageView = findViewById(R.id.imageDetail);

        // Establece una imagen por defecto
        imageView.setImageResource(R.drawable.img_5);

        // Obtener los datos enviados desde el Adapter
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            textTipo.setText(extras.getString("tipo", ""));
            textDescripcion.setText(extras.getString("descripcion", ""));
            textFecha.setText(extras.getString("fecha", ""));
            textEstatus.setText(extras.getString("estatus", ""));
            textResolucion.setText(extras.getString("resolucion", ""));

            String imagenBase64 = extras.getString("imagen", null);
            if (imagenBase64 != null && !imagenBase64.isEmpty()) {
                try {
                    byte[] decodedBytes = Base64.decode(imagenBase64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        imageView.setImageResource(R.drawable.img_5); // fallback imagen por defecto
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    imageView.setImageResource(R.drawable.img_5); // fallback imagen por defecto si hay error
                }
            } else {
                imageView.setImageResource(R.drawable.img_5); // fallback imagen por defecto si es null o vacía
            }
        }
    }
}
