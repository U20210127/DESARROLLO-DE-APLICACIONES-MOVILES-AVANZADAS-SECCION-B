package com.example.incidentes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.incidentes.utils.SessionManager;

public class SettingsFragment extends Fragment {

    private Switch switchDarkMode;
    private TextView textUserName;
    private ImageView imageAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        textUserName = view.findViewById(R.id.textUserName);
        imageAvatar = view.findViewById(R.id.imageAvatar); // Asegúrate que este ID esté en tu layout XML

        // Mostrar el nombre del usuario desde SessionManager
        String nombre = SessionManager.getUserName(requireContext());
        textUserName.setText(nombre);

        // Mostrar el avatar desde SessionManager
        cargarAvatarDesdeSession();

        // Cargar estado guardado de modo oscuro
        boolean isDarkMode = loadDarkModeState();
        switchDarkMode.setChecked(isDarkMode);

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            saveDarkModeState(isChecked);
        });

        return view;
    }

    private void saveDarkModeState(boolean isDarkMode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        prefs.edit().putBoolean("dark_mode", isDarkMode).apply();
    }

    private boolean loadDarkModeState() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return prefs.getBoolean("dark_mode", false);
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
                        .into(imageAvatar);
            } catch (Exception e) {
                e.printStackTrace();
                Glide.with(this)
                        .load(R.drawable.img)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageAvatar);
            }
        } else {
            Glide.with(this)
                    .load(R.drawable.img)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageAvatar);
        }
    }
}
