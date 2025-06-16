import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.incidentes.R;
import com.example.incidentes.models.Usuario;
import com.example.incidentes.network.ApiClient;
import com.example.incidentes.network.ApiService;
import com.example.incidentes.utils.SessionManager;

import cn.pedant.SweetAlert.SweetAlertDialog;
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

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Usuario> call = apiService.login(email, password);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("¡Bienvenido!")
                            .setContentText("Inicio de sesión exitoso")
                            .setConfirmClickListener(sDialog -> {
                                sDialog.dismissWithAnimation();
                                SessionManager.saveUser(LoginActivity.this, response.body());
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            })
                            .show();
                } else {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Credenciales incorrectas")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error de red")
                        .setContentText(t.getMessage())
                        .show();
            }
        });
    }
}
