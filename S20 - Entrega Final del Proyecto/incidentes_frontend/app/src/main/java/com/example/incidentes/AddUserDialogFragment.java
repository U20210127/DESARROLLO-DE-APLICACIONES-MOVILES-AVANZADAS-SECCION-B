package com.example.incidentes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.incidentes.network.ApiClient;
import com.example.incidentes.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserDialogFragment extends DialogFragment {

    private Spinner spinnerRol; // Spinner para mostrar los roles

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_user, container, false);

        // Obtener referencias de los campos
        final EditText etNombreUsuario = view.findViewById(R.id.edtName);
        final EditText etEmail = view.findViewById(R.id.edtEmail);
        final EditText etPassword = view.findViewById(R.id.edtPassword);
        spinnerRol = view.findViewById(R.id.spinnerRol);

        // Llamada a la API para obtener los roles
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<String>> callRoles = apiService.getRoles();

        // Realizamos la llamada para obtener los roles
        callRoles.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> roles = response.body();

                    // Agregamos "Seleccione un rol" como primera opción
                    roles.add(0, "Seleccione un rol");

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, roles);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerRol.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Error al cargar roles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Botón de Guardar
        Button btnGuardar = view.findViewById(R.id.btnSave);
        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombreUsuario.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String rol = spinnerRol.getSelectedItem().toString(); // Obtener el rol seleccionado

            // Crear objeto Usuario con rol (sin avatar)
            Usuario nuevoUsuario = new Usuario(nombre, email, password, rol);

            // Llamada Retrofit para crear el usuario
            ApiService apiServiceCreate = ApiClient.getClient().create(ApiService.class);
            Call<RespuestaUsuario> callCreateUser = apiServiceCreate.createUser(nuevoUsuario);

            callCreateUser.enqueue(new Callback<RespuestaUsuario>() {
                @Override
                public void onResponse(Call<RespuestaUsuario> call, Response<RespuestaUsuario> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        RespuestaUsuario r = response.body();
                        if (r.isSuccess()) {
                            Toast.makeText(getContext(), "Usuario creado con éxito", Toast.LENGTH_SHORT).show();
                            dismiss();
                            // Cierra el diálogo
                        } else {
                            Toast.makeText(getContext(), "Error: " + r.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al crear usuario. Respuesta inválida.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RespuestaUsuario> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setLayout(width, height);
        }
    }
}

