package com.example.incidentes.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.incidentes.R;
import com.example.incidentes.models.Incidente;

import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    private final List<Incidente> listaIncidencias;

    public DashboardAdapter(List<Incidente> listaIncidencias) {
        this.listaIncidencias = listaIncidencias;
    }

    @NonNull
    @Override
    public DashboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardAdapter.ViewHolder holder, int position) {
        Incidente incidente = listaIncidencias.get(position);

        holder.fechaTextView.setText("Fecha: " + incidente.getFecha_reportado());
        holder.usuarioTextView.setText("Usuario: " + incidente.getUsuario_nombre());
        holder.idTextView.setText("ID Incidencia: #" + incidente.getId());

        // Mostrar estado y cambiar el color según el estado
        if (holder.estadoTextView != null) {
            holder.estadoTextView.setText("Estado: " + incidente.getEstatus());

            switch (incidente.getEstatus().toLowerCase()) {
                case "activo":
                    holder.estadoTextView.setTextColor(Color.parseColor("#FFA500")); // naranja
                    break;
                case "resuelto":
                    holder.estadoTextView.setTextColor(Color.parseColor("#28A745")); // verde
                    break;
                case "solucionado":
                    holder.estadoTextView.setTextColor(Color.parseColor("#007BFF")); // azul
                    break;
                case "aplazado":
                    holder.estadoTextView.setTextColor(Color.parseColor("#FFC107")); // amarillo
                    break;
                case "revisado":
                    holder.estadoTextView.setTextColor(Color.parseColor("#6C757D")); // gris
                    break;
                case "eliminado":
                    holder.estadoTextView.setTextColor(Color.parseColor("#DC3545")); // rojo
                    break;
                default:
                    holder.estadoTextView.setTextColor(Color.BLACK); // negro si no se encuentra el estado
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return listaIncidencias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fechaTextView;
        TextView usuarioTextView;
        TextView idTextView;
        TextView estadoTextView; // agregado para mostrar estado

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fechaTextView = itemView.findViewById(R.id.incidencia_fecha);
            usuarioTextView = itemView.findViewById(R.id.incidencia_usuario);
            idTextView = itemView.findViewById(R.id.incidencia_id);

            // Asegurate de que este ID exista en item_dashboard.xml
            estadoTextView = itemView.findViewById(R.id.incidencia_estado);
        }
    }
}
