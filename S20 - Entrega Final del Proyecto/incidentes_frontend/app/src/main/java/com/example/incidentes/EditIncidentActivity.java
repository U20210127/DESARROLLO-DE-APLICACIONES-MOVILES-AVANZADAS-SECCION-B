package com.example.incidentes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.incidentes.models.Incidente;
import com.example.incidentes.network.ApiService;
import com.example.incidentes.network.RetrofitClient;
import com.example.incidentes.RespuestaUsuario;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditIncidentActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE_REQUEST = 1001;

    private EditText editEstatus, editResolucion;
    private Button btnGuardar;
    private ImageView imageView;

    private int incidenteId;
    private String imagenBase64 = null;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_incident);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            view.setPadding(view.getPaddingLeft(), insets.top, view.getPaddingRight(), view.getPaddingBottom());
            return windowInsets;
        });

        editEstatus = findViewById(R.id.editEstatus);
        editResolucion = findViewById(R.id.editResolucion);
        btnGuardar = findViewById(R.id.btnGuardar);
        imageView = findViewById(R.id.imagePreview); // <-- asegúrate de tener esto en el XML

        apiService = RetrofitClient.getApiService();

        incidenteId = getIntent().getIntExtra("id", -1);
        String resolucion = getIntent().getStringExtra("resolucion");

        editEstatus.setText("Resuelto");
        editEstatus.setEnabled(false);
        editResolucion.setText(resolucion);

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_IMAGE_REQUEST);
        });

        btnGuardar.setOnClickListener(v -> {
            String nuevoEstatus = editEstatus.getText().toString().trim();
            String nuevaResolucion = editResolucion.getText().toString().trim();

            if (nuevoEstatus.isEmpty() || nuevaResolucion.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            updateIncidenteStatus(incidenteId, nuevoEstatus, nuevaResolucion, imagenBase64);
        });
    }

    private void updateIncidenteStatus(int id, String nuevoStatus, String resolucion, String imagenBase64) {
        Incidente incidente = new Incidente(id, nuevoStatus, resolucion, imagenBase64);

        Call<RespuestaUsuario> call = apiService.updateIncidenteStatus(incidente);
        call.enqueue(new Callback<RespuestaUsuario>() {
            @Override
            public void onResponse(Call<RespuestaUsuario> call, Response<RespuestaUsuario> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(EditIncidentActivity.this, "Incidente actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditIncidentActivity.this, "Error al actualizar incidente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaUsuario> call, Throwable t) {
                Toast.makeText(EditIncidentActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] imageBytes = baos.toByteArray();
                imagenBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            } catch (Exception e) {
                Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}





