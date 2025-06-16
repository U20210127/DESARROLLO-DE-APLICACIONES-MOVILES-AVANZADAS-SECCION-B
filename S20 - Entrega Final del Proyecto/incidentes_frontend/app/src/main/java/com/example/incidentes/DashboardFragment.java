package com.example.incidentes;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.incidentes.adapters.DashboardAdapter;
import com.example.incidentes.models.Incidente;
import com.example.incidentes.models.Resumen;
import com.example.incidentes.models.RespuestaDashboard;
import com.example.incidentes.network.ApiService;
import com.example.incidentes.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private DashboardAdapter adapter;

    private TextView totalTextView;
    private TextView activosTextView;
    private TextView resueltosTextView;
    private TextView solucionadosTextView;
    private TextView aplazadosTextView;
    private TextView revisadosTextView;
    private TextView eliminadosTextView;

    public DashboardFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recyclerView = view.findViewById(R.id.incidenciasRecyclerView);
        totalTextView = view.findViewById(R.id.totalResumenTextView);
        activosTextView = view.findViewById(R.id.tvactivos);
        resueltosTextView = view.findViewById(R.id.tvresueltos);
        solucionadosTextView = view.findViewById(R.id.solucionado);
        aplazadosTextView = view.findViewById(R.id.aplazado);
        revisadosTextView = view.findViewById(R.id.revisado);
        eliminadosTextView = view.findViewById(R.id.eliminado);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);

        // Ajuste dinámico para muesca / barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            if (topInset > 0) {
                v.setPadding(0, topInset, 0, 0);
            }
            return insets.consumeSystemWindowInsets();
        });

        obtenerDatosDashboard();

        return view;
    }

    private void setStyledText(TextView textView, String number, String label) {
        String fullText = number + "\n" + label;
        SpannableString spannable = new SpannableString(fullText);
        spannable.setSpan(new RelativeSizeSpan(1.8f), 0, number.length(), 0); // Número más grande
        textView.setText(spannable);
    }

    private void obtenerDatosDashboard() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<RespuestaDashboard> call = apiService.getDashboardData();
        call.enqueue(new Callback<RespuestaDashboard>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaDashboard> call,
                                   @NonNull Response<RespuestaDashboard> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    // Mostrar resumen con números grandes
                    Resumen resumen = response.body().getResumen();
                    if (resumen != null) {
                        if (totalTextView != null)
                            setStyledText(totalTextView, String.valueOf(resumen.getTotal()), "Total Incidencias");
                        if (activosTextView != null)
                            setStyledText(activosTextView, String.valueOf(resumen.getActivos()), "Pendiente");
                        if (resueltosTextView != null)
                            setStyledText(resueltosTextView, String.valueOf(resumen.getResueltos()), "En Proceso");
                        if (solucionadosTextView != null)
                            setStyledText(solucionadosTextView, String.valueOf(resumen.getSolucionados()), "Solucionado");
                        if (aplazadosTextView != null)
                            setStyledText(aplazadosTextView, String.valueOf(resumen.getAplazados()), "Aplazado");
                        if (revisadosTextView != null)
                            setStyledText(revisadosTextView, String.valueOf(resumen.getRevisados()), "Revisado");
                        if (eliminadosTextView != null)
                            setStyledText(eliminadosTextView, String.valueOf(resumen.getEliminados()), "Eliminado");
                    }

                    // Filtrar incidentes por estados
                    List<Incidente> lista = response.body().getIncidentes();
                    List<Incidente> activos = new ArrayList<>();
                    for (Incidente incidente : lista) {
                        if ("Activo".equalsIgnoreCase(incidente.getEstatus()) ||
                                "Resuelto".equalsIgnoreCase(incidente.getEstatus()) ||
                                "Solucionado".equalsIgnoreCase(incidente.getEstatus()) ||
                                "Aplazado".equalsIgnoreCase(incidente.getEstatus()) ||
                                "Revisado".equalsIgnoreCase(incidente.getEstatus()) ||
                                "Eliminado".equalsIgnoreCase(incidente.getEstatus())) {
                            activos.add(incidente);
                        }
                    }

                    adapter = new DashboardAdapter(activos);
                    recyclerView.setAdapter(adapter);

                } else {
                    Log.e("DashboardFragment", "Respuesta vacía o no exitosa");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaDashboard> call, @NonNull Throwable t) {
                Log.e("DashboardFragment", "Error al obtener datos: " + t.getMessage());
            }
        });
    }
}




