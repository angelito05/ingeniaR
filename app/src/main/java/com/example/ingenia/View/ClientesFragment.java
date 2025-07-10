package com.example.ingenia.View;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.ingenia.Adapter.ClienteAdapter;
import com.example.ingenia.Model.Cliente;
import com.example.ingenia.R;
import com.example.ingenia.api.ApiConfig;
import com.example.ingenia.api.UsuarioService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientesFragment extends Fragment {

    private List<Cliente> listaOriginal; // Lista completa sin filtrar
    private ClienteAdapter adapter;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private int idUsuario;
    private UsuarioService service;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clientes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = view.findViewById(R.id.searchViewClientes);
        recyclerView = view.findViewById(R.id.recyclerClientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(UsuarioService.class);

        SharedPreferences prefs = requireActivity().getSharedPreferences("CrediGoPrefs", getContext().MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(getContext(), "Usuario no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        obtenerClientesPorUsuario();

        // Escucha para el buscador
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // No hacemos nada especial al enviar
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarClientes(newText);
                return true;
            }
        });
    }

    private void obtenerClientesPorUsuario() {
        service.getClientesPorUsuario(idUsuario).enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaOriginal = response.body();

                    adapter = new ClienteAdapter(new ArrayList<>(listaOriginal), new ClienteAdapter.OnItemClickListener() {
                        @Override
                        public void onVerDetallesClicked(Cliente cliente) {
                            DetalleClienteFragment fragment = new DetalleClienteFragment();
                            Bundle args = new Bundle();
                            args.putInt("id_cliente", cliente.idCliente);
                            fragment.setArguments(args);

                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container_fragment, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }

                        @Override
                        public void onAbrirSolicitudFinalClicked(Cliente cliente) {
                            abrirSolicitudFinal(cliente.idCliente);
                        }
                    });

                    recyclerView.setAdapter(adapter);

                } else {
                    Toast.makeText(getContext(), "No se pudieron obtener los clientes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filtrarClientes(String texto) {
        List<Cliente> filtrados = new ArrayList<>();
        for (Cliente cliente : listaOriginal) {
            if (cliente.nombre.toLowerCase().contains(texto.toLowerCase()) ||
                    cliente.apellido_paterno.toLowerCase().contains(texto.toLowerCase()) ||
                    cliente.apellido_materno.toLowerCase().contains(texto.toLowerCase()) ||
                    cliente.curp.toLowerCase().contains(texto.toLowerCase())) {
                filtrados.add(cliente);
            }
        }
        adapter.actualizarLista(filtrados);
    }


    private void abrirSolicitudFinal(int idCliente) {
        SolicitudFinalFragment fragment = new SolicitudFinalFragment();

        Bundle args = new Bundle();
        args.putInt("id_cliente", idCliente);

        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }
}
