package com.example.incidentes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.incidentes.utils.SessionManager;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Obtener el nombre del usuario desde SessionManager
        String userName = SessionManager.getUserName(requireContext());

        if (userName != null && !userName.isEmpty()) {
            TextView usernameTextView = view.findViewById(R.id.user_name);

            // Obtener la hora actual
            int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

            // Determinar el saludo según la hora
            String saludo = obtenerSaludo(hourOfDay);

            // Mostrar el saludo con el nombre del usuario
            usernameTextView.setText(saludo + ", " + userName);
        }

        return view;
    }

    // Método para obtener el saludo según la hora del día
    private String obtenerSaludo(int hourOfDay) {
        if (hourOfDay >= 6 && hourOfDay < 12) {
            return "Buenos días";
        } else if (hourOfDay >= 12 && hourOfDay < 19) {
            return "Buenas tardes";
        } else {
            return "Buenas noches";
        }
    }
}

