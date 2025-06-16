package com.example.incidentes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.incidentes.network.ApiClient;
import com.example.incidentes.network.ApiService;
import com.example.incidentes.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText editNombre, editEmail, editRol;
    private Button btnGuardar, btnCancelar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        // Toolbar + Drawer setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        // Ocultar opciones según el rol
        String rolUsuario = SessionManager.getUserRole(this);
        Menu menu = navView.getMenu();

        if ("empleado".equalsIgnoreCase(rolUsuario)) {
            menu.findItem(R.id.nav_usuarios).setVisible(false);
        }

        if (!"administrador".equalsIgnoreCase(rolUsuario)) {
            menu.findItem(R.id.nav_reportes).setVisible(false);
            menu.findItem(R.id.nav_dashboard).setVisible(false);
        }

        // Inicializar campos
        editNombre = findViewById(R.id.editNombre);
        editEmail = findViewById(R.id.editEmail);
        editRol = findViewById(R.id.editRol);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Cargar datos del usuario desde el Intent
        Intent intent = getIntent();
        int usuarioId = intent.getIntExtra("usuario_id", -1);
        String nombre = intent.getStringExtra("usuario_nombre");
        String email = intent.getStringExtra("usuario_email");
        String rol = intent.getStringExtra("usuario_rol");

        editNombre.setText(nombre);
        editEmail.setText(email);
        editRol.setText(rol);

        // Guardar cambios
        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = editNombre.getText().toString();
            String nuevoEmail = editEmail.getText().toString();
            String nuevoRol = editRol.getText().toString();

            if (nuevoNombre.isEmpty() || nuevoEmail.isEmpty() || nuevoRol.isEmpty()) {
                Toast.makeText(this, "Todos los campos deben ser completados", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario usuario = new Usuario(usuarioId, nuevoNombre, nuevoEmail, "", nuevoRol);
            ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
            Call<RespuestaUsuario> call = apiService.updateUsuario(usuario);

            call.enqueue(new Callback<RespuestaUsuario>() {
                @Override
                public void onResponse(Call<RespuestaUsuario> call, Response<RespuestaUsuario> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(EditarActivity.this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditarActivity.this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RespuestaUsuario> call, Throwable t) {
                    Toast.makeText(EditarActivity.this, "Error en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Cancelar y volver al fragmento de usuarios
        btnCancelar.setOnClickListener(v -> {
            Fragment fragment = new UsuariosFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            drawerLayout.closeDrawers();
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        int id = menuItem.getItemId();

        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_profile) {
            fragment = new ProfileFragment();
        } else if (id == R.id.nav_settings) {
            fragment = new SettingsFragment();
        } else if (id == R.id.nav_dashboard) {
            fragment = new DashboardFragment();
        } else if (id == R.id.nav_usuarios) {
            fragment = new UsuariosFragment();
        } else if (id == R.id.nav_incidentes) {
            fragment = new IncidentesFragment();
        } else if (id == R.id.nav_reportes) {
            fragment = new ReportesFragments();
        } else if (id == R.id.nav_logout) {
            SessionManager.logoutUser(this);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(menuItem.getTitle());
            }
        }

        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
