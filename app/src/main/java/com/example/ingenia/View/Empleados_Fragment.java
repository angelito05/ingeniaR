package com.example.ingenia.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ingenia.Adapter.UsuarioAdapter;
import com.example.ingenia.Model.User;
import com.example.ingenia.R;
import com.example.ingenia.api.ApiConfig;
import com.example.ingenia.api.UsuarioService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Empleados_Fragment extends Fragment {
    private RecyclerView recyclerView;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Llamar a la API para obtener los usuarios
        UsuarioService api = ApiConfig.getRetrofit().create(UsuarioService.class);
        api.obtenerTodosLosUsuarios().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> listaUsuarios = response.body(); // Esta variable está declarada localmente aquí
                    Log.d("API", "Usuarios recibidos: " + listaUsuarios.size());

                    UsuarioAdapter adapter = new UsuarioAdapter(requireContext(), listaUsuarios);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "No se pudieron cargar los usuarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}