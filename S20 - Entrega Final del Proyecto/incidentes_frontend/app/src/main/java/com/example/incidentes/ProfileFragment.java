package com.example.incidentes;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build; // ✅ NUEVO
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.incidentes.network.ApiClient;
import com.example.incidentes.network.ApiService;
import com.example.incidentes.RespuestaUsuario;
import com.example.incidentes.Usuario;
import com.example.incidentes.utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1002;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1003;

    private ImageView imageProfile;
    private ImageView transportBike;
    private TextView textName, textEmail, textRole, textLocation;
    private FusedLocationProviderClient fusedLocationClient;

    private BottomSheetDialog dialog;
    private ImageView imagePreview;
    private String base64Avatar = null;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Cambiar color de la status bar
        if (getActivity() != null) {
            TypedValue typedValue = new TypedValue();
            if (requireContext().getTheme().resolveAttribute(android.R.attr.statusBarColor, typedValue, true)) {
                int color = ContextCompat.getColor(requireContext(), typedValue.resourceId);
                getActivity().getWindow().setStatusBarColor(color);
            }
        }

        imageProfile = view.findViewById(R.id.imageProfile);
        textName = view.findViewById(R.id.textName);
        textEmail = view.findViewById(R.id.textEmail);
        textRole = view.findViewById(R.id.textRole);
        textLocation = view.findViewById(R.id.textLocation);
        transportBike = view.findViewById(R.id.transportBike);


        textName.setText(SessionManager.getUserName(requireContext()));
        textEmail.setText(SessionManager.getUserEmail(requireContext()));
        textRole.setText(SessionManager.getUserRole(requireContext()));

        cargarAvatarDesdeSession();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        requestLocation();

        FloatingActionButton fabEditImage = view.findViewById(R.id.fabEditImage);
        fabEditImage.setOnClickListener(v -> showEditAvatarDialog());

        // Inicializar ActivityResultLaunchers
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        if (bitmap != null) {
                            imagePreview.setImageBitmap(bitmap);
                            base64Avatar = convertirBitmapABase64(bitmap);
                        }
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                                imagePreview.setImageBitmap(bitmap);
                                base64Avatar = convertirBitmapABase64(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        return view;
    }

    private void cargarAvatarDesdeSession() {
        String avatarBase64 = SessionManager.getUserAvatar(requireContext());



        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
                Glide.with(this)
                        .load(decodedBytes)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.img)
                                .error(R.drawable.img)
                                .circleCrop())
                        .into(imageProfile);

                Glide.with(this)
                        .load(decodedBytes)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.img)
                                .error(R.drawable.img)
                                .circleCrop())
                        .into(transportBike);
            } catch (Exception e) {
                e.printStackTrace();
                // En caso de error, imagen por defecto circular en ambos
                Glide.with(this)
                        .load(R.drawable.img)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageProfile);

                Glide.with(this)
                        .load(R.drawable.img)
                        .apply(RequestOptions.circleCropTransform())
                        .into(transportBike);
            }
        } else {
            // No hay avatar, usar imagen por defecto circular en ambos
            Glide.with(this)
                    .load(R.drawable.img)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageProfile);

            Glide.with(this)
                    .load(R.drawable.img)
                    .apply(RequestOptions.circleCropTransform())
                    .into(transportBike);
        }
    }




    private void requestLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation();
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        setLocationText(location);
                    } else {
                        textLocation.setText("Ubicación no disponible");
                    }
                });
    }

    private void setLocationText(android.location.Location location) {
        android.location.Geocoder geocoder = new android.location.Geocoder(requireContext(), Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                android.location.Address address = addresses.get(0);
                String city = address.getLocality();
                String country = address.getCountryName();
                textLocation.setText(country + " - " + city);
            } else {
                textLocation.setText("Ubicación desconocida");
            }
        } catch (IOException e) {
            e.printStackTrace();
            textLocation.setText("Error obteniendo ubicación");
        }
    }

    private void showEditAvatarDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.modal_update_avatar, null);
        dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(dialogView);

        LinearLayout optionCamera = dialogView.findViewById(R.id.optionCamera);
        LinearLayout optionGallery = dialogView.findViewById(R.id.optionGallery);
        imagePreview = dialogView.findViewById(R.id.imagePreview);
        Button btnUpload = dialogView.findViewById(R.id.btnUpload);

        base64Avatar = null;

        optionCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                abrirCamara();
            }
        });

        optionGallery.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_REQUEST_CODE);
                } else {
                    abrirGaleria();
                }
            } else {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
                } else {
                    abrirGaleria();
                }
            }
        });

        btnUpload.setOnClickListener(v -> {
            if (base64Avatar != null) {
                subirAvatar(base64Avatar);
            } else {
                Toast.makeText(requireContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void subirAvatar(String base64) {
        int userId = SessionManager.getUserId(requireContext());
        String nombre = SessionManager.getUserName(requireContext());
        String email = SessionManager.getUserEmail(requireContext());
        String rol = SessionManager.getUserRole(requireContext());

        // Crear usuario con campos completos para evitar fallos en backend
        Usuario usuario = new Usuario(userId, nombre, email, null, rol, base64);

        // Convertir el objeto Usuario a JSON para imprimir en log (usa Gson)
        Gson gson = new Gson();
        String usuarioJson = gson.toJson(usuario);
        Log.d("DEBUG", "JSON que se enviará: " + usuarioJson);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RespuestaUsuario> call = apiService.updateUsuario(usuario);
        call.enqueue(new Callback<RespuestaUsuario>() {
            @Override
            public void onResponse(Call<RespuestaUsuario> call, Response<RespuestaUsuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Avatar actualizado", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    SessionManager.saveUserAvatar(requireContext(), base64);
                    cargarAvatarDesdeSession();
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar avatar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaUsuario> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String convertirBitmapABase64(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] bytes = stream.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            boolean granted = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                granted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
            } else {
                granted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            }

            if (granted) {
                abrirGaleria();
            } else {
                Toast.makeText(requireContext(), "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

