package com.example.incidentes;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;  // IMPORT PARA TEMA OSCURO
import androidx.appcompat.widget.Toolbar;
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

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Aquí ya no es necesario validar la sesión en onCreate(), lo movemos a onStart()

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mi App");
        }

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navView = findViewById(R.id.nav_view);

        // 🔐 Ocultar opciones del drawer según el rol
        String rol = SessionManager.getUserRole(this);
        Menu menu = navView.getMenu();

        if ("empleado".equalsIgnoreCase(rol)) {
            menu.findItem(R.id.nav_usuarios).setVisible(false);
        }

        if (!"administrador".equalsIgnoreCase(rol)) {
            menu.findItem(R.id.nav_reportes).setVisible(false);
        }
        if (!"administrador".equalsIgnoreCase(rol)) {
            menu.findItem(R.id.nav_dashboard).setVisible(false);
        }

        // 🧭 Navegación de fragmentos
        navView.setNavigationItemSelectedListener(menuItem -> {
            Fragment fragment = null;
            int id = menuItem.getItemId();

            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            } else if (id == R.id.nav_settings) {
                fragment = new SettingsFragment();
            } else if (id == R.id.nav_dashboard) {
                fragment = new DashboardFragment(); // 👈 NUEVO fragmento referenciado
            } else if (id == R.id.nav_usuarios) {
                fragment = new UsuariosFragment();
            } else if (id == R.id.nav_incidentes) {
                fragment = new IncidentesFragment();
            } else if (id == R.id.nav_reportes) {
                fragment = new ReportesFragments();
            } else if (id == R.id.nav_logout) {
                SessionManager.logoutUser(this);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }

            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, fragment);
                transaction.commit();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(menuItem.getTitle());
                }
            }

            menuItem.setChecked(true);
            drawerLayout.closeDrawers();
            return true;
        });

        // Mostrar nombre del usuario, rol y avatar en el header
        String userName = SessionManager.getUserName(this);
        String userRole = SessionManager.getUserRole(this);

        if (!userName.isEmpty()) {
            View headerView = navView.getHeaderView(0);
            TextView usernameTextView = headerView.findViewById(R.id.user_name);
            usernameTextView.setText(userName);

            TextView userRoleTextView = headerView.findViewById(R.id.user_role);
            userRoleTextView.setText(" " + userRole);

            ImageView avatarImageView = headerView.findViewById(R.id.img_avatar);
            String base64Avatar = SessionManager.getUserAvatar(this);

            if (base64Avatar != null && !base64Avatar.isEmpty()) {
                try {
                    byte[] decodedBytes = android.util.Base64.decode(base64Avatar, android.util.Base64.DEFAULT);
                    setCircularAvatar(avatarImageView, decodedBytes);
                } catch (Exception e) {
                    e.printStackTrace();
                    avatarImageView.setImageResource(R.drawable.img); // Imagen por defecto en caso de error
                }
            } else {
                avatarImageView.setImageResource(R.drawable.img); // Imagen por defecto si no hay avatar
            }

            // --- TOGGLE TEMA ---
            ImageView toggleTheme = headerView.findViewById(R.id.img_toggle_theme);

            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                toggleTheme.setImageResource(R.drawable.img_9);  // icono sol para modo oscuro
            } else {
                toggleTheme.setImageResource(R.drawable.img_8); // icono luna para modo claro
            }

            toggleTheme.setOnClickListener(v -> {
                int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (mode == Configuration.UI_MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            });
            // --- FIN TOGGLE ---
        }

        fetchUsuarioByNombre(userName);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new HomeFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Validar sesión activa antes de continuar
        if (SessionManager.getUserId(this) == -1) {
            // Si la sesión no está activa, redirigir al login
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpiar la pila de actividades
            startActivity(loginIntent);
            finish(); // Termina la actividad actual
        }
    }

    private void fetchUsuarioByNombre(String nombreUsuario) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Usuario> call = apiService.getUsuarioByNombre(nombreUsuario);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();
                    Toast.makeText(MainActivity.this, "Usuario: " + usuario.getNombre(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    // ✅ Función para hacer la imagen circular
    private void setCircularAvatar(ImageView imageView, byte[] imageBytes) {
        try {
            Bitmap originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            if (originalBitmap != null) {
                int size = Math.min(originalBitmap.getWidth(), originalBitmap.getHeight());
                int x = (originalBitmap.getWidth() - size) / 2;
                int y = (originalBitmap.getHeight() - size) / 2;

                Bitmap squaredBitmap = Bitmap.createBitmap(originalBitmap, x, y, size, size);
                Bitmap circularBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(circularBitmap);
                Paint paint = new Paint();
                Rect rect = new Rect(0, 0, size, size);

                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                paint.setDither(true);

                canvas.drawARGB(0, 0, 0, 0);
                canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(squaredBitmap, rect, rect, paint);

                imageView.setImageBitmap(circularBitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




