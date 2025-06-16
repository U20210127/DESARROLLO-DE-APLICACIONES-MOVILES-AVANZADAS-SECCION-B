package com.example.incidentes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.incidentes.network.ApiClient;
import com.example.incidentes.network.ApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportesFragments extends Fragment {

    private MaterialButton btnGenerateReport;
    private String ultimoTipoReporte;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportes_fragments, container, false);
        btnGenerateReport = view.findViewById(R.id.btn_generate_report);

        btnGenerateReport.setOnClickListener(v -> openGenerateReportDialog());

        return view;
    }

    private void openGenerateReportDialog() {
        GenerateReportDialogFragment dialog = new GenerateReportDialogFragment();
        dialog.setOnReportGenerateListener((tipoReporte, extension) -> {
            this.ultimoTipoReporte = tipoReporte;
            descargarReporte(tipoReporte);
        });

        dialog.show(getParentFragmentManager(), "generate_report_dialog");
    }

    private void descargarReporte(String tipoReporte) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = tipoReporte.equals("activos")
                ? apiService.descargarReporteActivos()
                : apiService.descargarReporteResueltos();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean resultado = guardarArchivoPdf(response.body(), tipoReporte);
                    if (resultado) {
                        Toast.makeText(getContext(), "Reporte guardado en almacenamiento interno", Toast.LENGTH_LONG).show();
                        mostrarNotificacion(tipoReporte);
                    } else {
                        Toast.makeText(getContext(), "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error al descargar el reporte", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean guardarArchivoPdf(ResponseBody body, String tipoReporte) {
        try {
            File destino = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                    "reporte_" + tipoReporte + ".pdf");

            InputStream inputStream = body.byteStream();
            OutputStream outputStream = new FileOutputStream(destino);

            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void mostrarNotificacion(String tipoReporte) {
        String canalId = "reporte_descargado";
        String canalNombre = "Descarga de Reportes";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    canalId,
                    canalNombre,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Android 13+: Verificación de permiso de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                return;
            }
        }

        File pdfFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                "reporte_" + tipoReporte + ".pdf");

        Uri uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".provider",
                pdfFile
        );

        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setDataAndType(uri, "application/pdf");
        viewIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                requireContext(),
                0,
                viewIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), canalId)
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle("Reporte descargado")
                .setContentText("Toca para abrir el PDF.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify(1001, builder.build());
    }

    // Manejo del permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                mostrarNotificacion(ultimoTipoReporte);
            } else {
                Toast.makeText(getContext(), "Permiso de notificación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class GenerateReportDialogFragment extends DialogFragment {

        public interface OnReportGenerateListener {
            void onReportGenerate(String tipoReporte, String extension);
        }

        private OnReportGenerateListener listener;

        public void setOnReportGenerateListener(OnReportGenerateListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());

            View dialogView = requireActivity().getLayoutInflater()
                    .inflate(R.layout.dialog_generate_report, null);

            RadioGroup radioGroup = dialogView.findViewById(R.id.rg_tipo_reporte);
            MaterialCheckBox cbPdf = dialogView.findViewById(R.id.cb_pdf);

            builder.setView(dialogView)
                    .setPositiveButton("Generar", (dialog, which) -> {
                        int checkedId = radioGroup.getCheckedRadioButtonId();
                        String tipoReporte;

                        if (checkedId == R.id.rb_reportes_activos) {
                            tipoReporte = "activos";
                        } else if (checkedId == R.id.rb_reportes_resueltos) {
                            tipoReporte = "resueltos";
                        } else {
                            Toast.makeText(getContext(), "Selecciona un tipo de reporte", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String extension = cbPdf.isChecked() ? "pdf" : "";
                        if (listener != null) {
                            listener.onReportGenerate(tipoReporte, extension);
                        }
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

            return builder.create();
        }
    }
}

