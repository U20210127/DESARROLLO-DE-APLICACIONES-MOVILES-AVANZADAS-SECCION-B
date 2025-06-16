package com.example.incidentes.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.incidentes.R;
import com.example.incidentes.models.Incidente;
import com.example.incidentes.DetailActivity;
import com.example.incidentes.EditIncidentActivity; // Importa tu nueva actividad de edición

import java.util.List;

public class IncidenteAdapter extends RecyclerView.Adapter<IncidenteAdapter.IncidenteViewHolder> {

    private Context context;
    private List<Incidente> incidentes;
    private String userRole;

    public IncidenteAdapter(Context context, List<Incidente> incidentes, String userRole) {
        this.context = context;
        this.incidentes = incidentes;
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public IncidenteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_incidente, parent, false);
        return new IncidenteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncidenteViewHolder holder, int position) {
        Incidente incidente = incidentes.get(position);

        holder.textTitulo.setText(incidente.getTipo());
        holder.textDescripcion.setText(incidente.getDescripcion());
        holder.textFecha.setText(incidente.getFecha());
        holder.textEstatus.setText(incidente.getEstatus());
        holder.textResolucion.setText(incidente.getResolucion());

        // Imagen
        String imagenBase64 = incidente.getImagen();
        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
            byte[] decodedString = Base64.decode(imagenBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.imageView.setImageBitmap(decodedByte);
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }

        // Mostrar u ocultar botón "Editar" según el rol
        if ("administrador".equalsIgnoreCase(userRole)) {
            holder.btnEditar.setVisibility(View.VISIBLE);
        } else {
            holder.btnEditar.setVisibility(View.GONE);
        }

        // Ir a DetailActivity al hacer clic en el CardView
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("tipo", incidente.getTipo());
            intent.putExtra("descripcion", incidente.getDescripcion());
            intent.putExtra("fecha", incidente.getFecha());
            intent.putExtra("estatus", incidente.getEstatus());
            intent.putExtra("resolucion", incidente.getResolucion());
            intent.putExtra("imagen", incidente.getImagen()); // Base64 string

            context.startActivity(intent);
        });

        // Acción para el botón Editar
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditIncidentActivity.class);
            intent.putExtra("id", incidente.getId()); // Asegúrate que Incidente tiene getId()
            intent.putExtra("estatus", incidente.getEstatus());
            intent.putExtra("resolucion", incidente.getResolucion());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return incidentes.size();
    }

    public static class IncidenteViewHolder extends RecyclerView.ViewHolder {
        TextView textTitulo, textDescripcion, textFecha, textEstatus, textResolucion;
        CardView cardView;
        ImageView imageView;
        Button btnEditar;

        public IncidenteViewHolder(View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.textTipo);
            textDescripcion = itemView.findViewById(R.id.textDescripcion);
            textFecha = itemView.findViewById(R.id.textFecha);
            textEstatus = itemView.findViewById(R.id.textEstatus);
            textResolucion = itemView.findViewById(R.id.textResolucion);
            cardView = itemView.findViewById(R.id.cardViewIncidente);
            imageView = itemView.findViewById(R.id.imageIncidente);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }
}
