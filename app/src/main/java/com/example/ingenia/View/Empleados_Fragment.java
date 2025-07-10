package com.example.ingenia.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ingenia.Adapter.UsuarioAdapter;
import com.example.ingenia.Model.User;
import com.example.ingenia.R;
import com.example.ingenia.api.ApiConfig;
import com.example.ingenia.api.UsuarioService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Empleados_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private List<User> listaUsuariosOriginal;
    private UsuarioAdapter adapter;

    public Empleados_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empleados, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerPromotores);
        searchView = view.findViewById(R.id.searchViewUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        UsuarioService api = ApiConfig.getRetrofit().create(UsuarioService.class);

        api.obtenerTodosLosUsuarios().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!isAdded() || response.body() == null) return;

                listaUsuariosOriginal = new ArrayList<>(response.body());
                adapter = new UsuarioAdapter(requireContext(), new ArrayList<>(listaUsuariosOriginal));
                recyclerView.setAdapter(adapter);
                Log.d("API", "Usuarios recibidos: " + listaUsuariosOriginal.size());

                configurarBusqueda();
            }


            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void configurarBusqueda() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // No necesitamos acción especial al presionar "buscar"
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarUsuarios(newText);
                return true;
            }
        });
    }

    private void filtrarUsuarios(String texto) {
        if (texto.isEmpty()) {
            adapter.actualizarLista(new ArrayList<>(listaUsuariosOriginal)); // copia para evitar errores
            return;
        }

        List<User> listaFiltrada = new ArrayList<>();
        for (User user : listaUsuariosOriginal) {
            if (user.getCorreo().toLowerCase().contains(texto.toLowerCase()) ||
                    user.getUsername().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(user);
            }
        }

        adapter.actualizarLista(listaFiltrada);
    }


}
