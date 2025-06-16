package com.example.incidentes;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.incidentes.adapters.UsuarioAdapter;
import com.example.incidentes.network.ApiClient;
import com.example.incidentes.network.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.SearchView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuariosFragment extends Fragment {

    private RecyclerView recyclerViewUsuarios;
    private UsuarioAdapter usuarioAdapter;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuarios, container, false);

        recyclerViewUsuarios = view.findViewById(R.id.recyclerViewUsuarios);
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fabAddUsuario = view.findViewById(R.id.fab_add_usuario);
        fabAddUsuario.setOnClickListener(v -> {
            AddUserDialogFragment dialog = new AddUserDialogFragment();
            dialog.show(getChildFragmentManager(), "AddUserDialog");
        });

        cargarUsuariosDesdeApi();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull android.view.Menu menu, @NonNull android.view.MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_usuarios, menu);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (usuarioAdapter != null) {
                    usuarioAdapter.filter(newText);
                }
                return false;
            }
        });
    }

    private void cargarUsuariosDesdeApi() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Usuario>> call = apiService.getAllUsuarios();

        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Usuario> usuariosList = response.body();
                    usuarioAdapter = new UsuarioAdapter(usuariosList, UsuariosFragment.this);
                    recyclerViewUsuarios.setAdapter(usuarioAdapter);
                } else {
                    Toast.makeText(getContext(), "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo en la red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void eliminarUsuario(final Usuario usuario, final int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar a este usuario?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    ApiService apiService = ApiClient.getClient().create(ApiService.class);

                    // Enviar el usuario como objeto (solo con el ID)
                    Call<RespuestaUsuario> call = apiService.deleteUsuario(new Usuario(usuario.getId()));

                    call.enqueue(new Callback<RespuestaUsuario>() {
                        @Override
                        public void onResponse(Call<RespuestaUsuario> call, Response<RespuestaUsuario> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                usuarioAdapter.removeUsuario(position);
                                Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Error al eliminar el usuario", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<RespuestaUsuario> call, Throwable t) {
                            Toast.makeText(getContext(), "Fallo en la red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}

