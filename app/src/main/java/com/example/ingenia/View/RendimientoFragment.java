package com.example.ingenia.View;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.ingenia.Adapter.SolicitudAdapter;
import com.example.ingenia.Model.SolicitudCredito;
import com.example.ingenia.R;
import com.example.ingenia.api.ApiConfig;
import com.example.ingenia.api.UsuarioService;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class RendimientoFragment extends Fragment {

    private RecyclerView recyclerView;
    private int idUsuario;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rendimiento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerRendimiento);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Obtener ID del usuario desde SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("CrediGoPrefs", getContext().MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(getContext(), "Usuario no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        obtenerSolicitudesUsuario();
    }

    private void obtenerSolicitudesUsuario() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        UsuarioService service = retrofit.create(UsuarioService.class);

        service.obtenerSolicitudesPorUsuario(idUsuario).enqueue(new Callback<List<SolicitudCredito>>() {
            @Override
            public void onResponse(Call<List<SolicitudCredito>> call, Response<List<SolicitudCredito>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SolicitudCredito> solicitudes = response.body();
                    recyclerView.setAdapter(new SolicitudAdapter(solicitudes, false));
                } else {
                    Toast.makeText(getContext(), "No se pudieron obtener las solicitudes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SolicitudCredito>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
