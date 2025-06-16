package com.example.incidentes.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.incidentes.R;
import com.example.incidentes.Usuario;
import com.example.incidentes.EditarActivity;  // Asegúrate de importar la actividad de edición
import com.example.incidentes.UsuariosFragment; // Importar el fragmento para la eliminación

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Usuario> usuariosList;
    private List<Usuario> usuariosListFull; // Lista completa de usuarios (para hacer el filtrado)
    private UsuariosFragment fragment; // Referencia al fragmento para eliminar usuarios

    public UsuarioAdapter(List<Usuario> usuariosList, UsuariosFragment fragment) {
        this.usuariosList = usuariosList;
        this.usuariosListFull = new ArrayList<>(usuariosList);
        this.fragment = fragment;
    }

    @Override
    public UsuarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsuarioViewHolder holder, int position) {
        Usuario usuario = usuariosList.get(position);
        holder.name.setText(usuario.getNombre());
        holder.email.setText(usuario.getEmail());
        holder.rol.setText(usuario.getRol());  // Mostrar el rol en el TextView

        // Configurar el OnClickListener para el botón de Editar
        holder.btnEdit.setOnClickListener(v -> {
            // Crear un Intent para ir a la actividad de editar
            Intent intent = new Intent(v.getContext(), EditarActivity.class);

            // Pasar los datos del usuario para que la actividad de editar pueda acceder a ellos
            intent.putExtra("usuario_id", usuario.getId());  // Asegúrate de que el usuario tenga un ID único
            intent.putExtra("usuario_nombre", usuario.getNombre());
            intent.putExtra("usuario_email", usuario.getEmail());
            intent.putExtra("usuario_rol", usuario.getRol());

            // Iniciar la actividad de editar
            v.getContext().startActivity(intent);
        });

        // Configurar el OnClickListener para el botón de Eliminar
        holder.btnDelete.setOnClickListener(v -> {
            // Llamar al método eliminarUsuario del fragmento
            fragment.eliminarUsuario(usuario, position);
        });
    }

    @Override
    public int getItemCount() {
        return usuariosList.size();
    }

    // Método para actualizar la lista de usuarios con el nuevo usuario
    public void addUsuario(Usuario usuario) {
        usuariosList.add(usuario);
        usuariosListFull.add(usuario); // Agregar también a la lista completa para el filtrado
        notifyItemInserted(usuariosList.size() - 1);
    }

    // Método para filtrar la lista de usuarios según la consulta
    public void filter(String query) {
        // Si la consulta está vacía, mostramos todos los usuarios
        if (query.isEmpty()) {
            usuariosList.clear();
            usuariosList.addAll(usuariosListFull);
        } else {
            List<Usuario> filteredList = new ArrayList<>();
            for (Usuario usuario : usuariosListFull) {
                // Comparamos el nombre y el email con la consulta (puedes cambiar esto si quieres comparar otros campos)
                if (usuario.getNombre().toLowerCase().contains(query.toLowerCase()) ||
                        usuario.getEmail().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(usuario);
                }
            }
            usuariosList.clear();
            usuariosList.addAll(filteredList);
        }
        notifyDataSetChanged(); // Notificar que los datos han cambiado
    }

    // Método para eliminar un usuario de la lista
    public void removeUsuario(int position) {
        usuariosList.remove(position);
        notifyItemRemoved(position);
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, rol;  // Agregar TextView para el rol
        MaterialButton btnEdit;  // Referencia al botón de editar
        MaterialButton btnDelete;  // Referencia al botón de eliminar

        public UsuarioViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            email = itemView.findViewById(R.id.textEmail);
            rol = itemView.findViewById(R.id.textRole);  // Inicializar el TextView para el rol
            btnEdit = itemView.findViewById(R.id.btnEdit);  // Inicializar el botón de editar
            btnDelete = itemView.findViewById(R.id.btnDelete);  // Inicializar el botón de eliminar
        }
    }
}
