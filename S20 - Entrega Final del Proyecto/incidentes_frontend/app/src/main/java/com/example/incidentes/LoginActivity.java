package com.example.incidentes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.incidentes.network.ApiClient;
import com.example.incidentes.network.ApiService;
import com.example.incidentes.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        // Validar que los campos no estén vacíos
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Por favor ingresa ambos campos.");
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Usuario> call = apiService.login(email, password);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Guardar datos en SharedPreferences
                    SessionManager.saveUser(LoginActivity.this, response.body());

                    // Ir siempre a MainActivity, el rol se maneja allá
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    showAlert("Error", "Credenciales incorrectas");
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                showAlert("Error de red", "No se pudo conectar: " + t.getMessage());
            }
        });
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
