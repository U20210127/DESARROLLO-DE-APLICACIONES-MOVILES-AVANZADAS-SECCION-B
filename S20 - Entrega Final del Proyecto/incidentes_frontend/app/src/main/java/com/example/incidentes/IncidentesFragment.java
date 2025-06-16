package com.example.incidentes;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.incidentes.adapters.IncidenteAdapter;
import com.example.incidentes.models.Incidente;
import com.example.incidentes.network.ApiService;
import com.example.incidentes.network.RetrofitClient;
import com.example.incidentes.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncidentesFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String CHANNEL_ID = "incidentes_channel";
    private static final int NOTIFICATION_ID = 1001;

    private RecyclerView recyclerViewIncidentes;
    private IncidenteAdapter incidenteAdapter;

    // Variables para manejar la imagen seleccionada
    private String imagenBase64 = null;
    private ImageView imageViewPreviewGlobal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incidentes, container, false);

        recyclerViewIncidentes = view.findViewById(R.id.recyclerViewIncidentes);
        recyclerViewIncidentes.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fab = view.findViewById(R.id.fab_add_incidente);
        fab.setOnClickListener(v -> mostrarDialogoRegistro());

        createNotificationChannel();

        cargarIncidentesDesdeApi();

        return view;
    }

    private void createNotificationChannel() {
        // Crear canal para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Incidentes Channel";
            String description = "Notificaciones para nuevos incidentes";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void mostrarNotificacionNuevoIncidente(String tipo, String descripcion, String fecha, String estatus, String resolucion, String imagenBase64) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // Si la imagen es nula o vacía, usar imagen por defecto en base64
        if (imagenBase64 == null || imagenBase64.trim().isEmpty()) {
            imagenBase64 = getImagenBase64PorDefecto(); // Este método lo defines abajo
        }



        Intent intent = new Intent(requireContext(), DetailActivity.class);
        intent.putExtra("tipo", tipo);
        intent.putExtra("descripcion", descripcion);
        intent.putExtra("fecha", fecha);
        intent.putExtra("estatus", estatus);
        intent.putExtra("resolucion", resolucion != null ? resolucion : "");
        intent.putExtra("imagen", imagenBase64);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Nuevo Incidente Registrado")
                .setContentText(tipo + ": " + descripcion)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private String getImagenBase64PorDefecto() {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_5); // Imagen predeterminada
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // En caso de error, se envía cadena vacía
        }
    }



    private void cargarIncidentesDesdeApi() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        String userRole = SessionManager.getUserRole(getContext());

        if ("administrador".equals(userRole)) {
            Call<List<Incidente>> call = apiService.getAllIncidentes();

            call.enqueue(new Callback<List<Incidente>>() {
                @Override
                public void onResponse(Call<List<Incidente>> call, Response<List<Incidente>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Incidente> incidenteList = response.body();
                        incidenteAdapter = new IncidenteAdapter(getContext(), incidenteList, SessionManager.getUserRole(getContext()));

                        recyclerViewIncidentes.setAdapter(incidenteAdapter);
                    } else {
                        Toast.makeText(getContext(), "Error al obtener incidentes", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Incidente>> call, Throwable t) {
                    Toast.makeText(getContext(), "Fallo en la red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } else if ("empleado".equals(userRole)) {
            int userId = SessionManager.getUserId(getContext());
            Call<List<Incidente>> call = apiService.getIncidentesByUser(userId);

            call.enqueue(new Callback<List<Incidente>>() {
                @Override
                public void onResponse(Call<List<Incidente>> call, Response<List<Incidente>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Incidente> incidenteList = response.body();

                        if (incidenteList.isEmpty()) {
                            showNoIncidentesAlert();
                        } else {
                            incidenteAdapter = new IncidenteAdapter(getContext(), incidenteList, SessionManager.getUserRole(getContext()));

                            recyclerViewIncidentes.setAdapter(incidenteAdapter);
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al obtener los incidentes del usuario", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Incidente>> call, Throwable t) {
                    Toast.makeText(getContext(), "Fallo en la red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Rol no reconocido", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNoIncidentesAlert() {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Sin incidentes")
                .setMessage("No tienes incidentes creados aún.")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Acción opcional
                })
                .setCancelable(false)
                .show();
    }

    private void mostrarDialogoRegistro() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_registrar_incidente, null);

        EditText editTextTipo = dialogView.findViewById(R.id.editTextTipo);
        EditText editTextDescripcion = dialogView.findViewById(R.id.editTextDescripcion);
        EditText editTextFecha = dialogView.findViewById(R.id.editTextFecha);
        EditText editTextUsuarioId = dialogView.findViewById(R.id.editTextUsuarioId);
        EditText editTextEstatus = dialogView.findViewById(R.id.editTextEstatus);
        MaterialButton buttonSeleccionarImagen = dialogView.findViewById(R.id.buttonSeleccionarImagen);
        ImageView imageViewPreview = dialogView.findViewById(R.id.imageViewPreview);

        // Guardamos la referencia para actualizar la vista previa al seleccionar imagen
        imageViewPreviewGlobal = imageViewPreview;

        // Rellenar el usuario ID con el usuario logueado y deshabilitar el campo para que no se modifique
        int userId = SessionManager.getUserId(getContext());
        editTextUsuarioId.setText(String.valueOf(userId));
        editTextUsuarioId.setEnabled(false);

        // Establecer valor por defecto "Activo" para el campo estatus
        editTextEstatus.setText("Activo");
        editTextEstatus.setEnabled(false); // Deshabilitar para evitar edición

        // Deshabilitar entrada directa y usar DatePickerDialog
        editTextFecha.setFocusable(false);
        editTextFecha.setClickable(true);
        editTextFecha.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                                selectedYear, selectedMonth + 1, selectedDay);
                        editTextFecha.setText(selectedDate);
                    }, year, month, day);

            datePickerDialog.show();
        });

        // Botón para seleccionar imagen
        buttonSeleccionarImagen.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
        });

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Registrar Incidente")
                .setView(dialogView)
                .setPositiveButton("Guardar", null) // Lo controlamos manualmente para validar
                .setNegativeButton("Cancelar", (d, w) -> {
                    imagenBase64 = null; // Limpiar en cancelar
                })
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String tipo = editTextTipo.getText().toString().trim();
                String descripcion = editTextDescripcion.getText().toString().trim();
                String fecha = editTextFecha.getText().toString().trim();
                String estatus = editTextEstatus.getText().toString().trim();
                String usuarioIdStr = editTextUsuarioId.getText().toString().trim();

                if (tipo.isEmpty() || descripcion.isEmpty() || fecha.isEmpty()
                        || estatus.isEmpty() || usuarioIdStr.isEmpty()) {
                    Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (imagenBase64 == null) {
                    Toast.makeText(getContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show();
                    return;
                }

                int usuario_id;
                try {
                    usuario_id = Integer.parseInt(usuarioIdStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Usuario ID inválido", Toast.LENGTH_SHORT).show();
                    return;
                }

                Incidente nuevoIncidente = new Incidente(tipo, descripcion, fecha, estatus, usuario_id, imagenBase64);

                ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                Call<RespuestaUsuario> call = apiService.createIncidente(nuevoIncidente);

                call.enqueue(new Callback<RespuestaUsuario>() {
                    @Override
                    public void onResponse(Call<RespuestaUsuario> call, Response<RespuestaUsuario> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(getContext(), "Incidente registrado exitosamente", Toast.LENGTH_SHORT).show();
                            cargarIncidentesDesdeApi();
                            dialog.dismiss();
                            imagenBase64 = null; // limpiar imagen después de registrar

                            // Mostrar notificación
                            // Mostrar notificación enviando todos los datos
                            mostrarNotificacionNuevoIncidente(tipo, descripcion, fecha, estatus, "", imagenBase64);

                        } else {
                            Toast.makeText(getContext(), "Error al registrar incidente", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaUsuario> call, Throwable t) {
                        Toast.makeText(getContext(), "Fallo en la red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            });
        });

        dialog.show();
    }

    // Capturamos resultado del selector de imagen
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imagenUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imagenUri);

                // Comprimir y convertir a Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] bytes = baos.toByteArray();
                imagenBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);

                // Mostrar vista previa
                if (imageViewPreviewGlobal != null) {
                    imageViewPreviewGlobal.setImageBitmap(bitmap);
                    imageViewPreviewGlobal.setVisibility(View.VISIBLE);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

