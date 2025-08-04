package com.credigo.ingenia.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.credigo.ingenia.Model.RegisterRequest;
import com.credigo.ingenia.Model.User;
import com.credigo.ingenia.R;
import com.credigo.ingenia.api.ApiConfig;
import com.credigo.ingenia.api.UsuarioService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrearFragment extends Fragment {
    private TextInputEditText editCorreo, editUsername, editPassword;
    private Spinner spinnerRol;
    private MaterialButton btnRegistrar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear, container, false);

        editCorreo = view.findViewById(R.id.editCorreo);
        editUsername = view.findViewById(R.id.editUsername);
        editPassword = view.findViewById(R.id.editPassword);
        spinnerRol = view.findViewById(R.id.spinnerRol);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_item,
                new String[]{"Administrador", "Trabajador"}
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // Para el menú desplegable
        spinnerRol.setAdapter(adapter);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
        return view;
    }

    private void registrarUsuario() {
        String correo = editCorreo.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        int idRol = spinnerRol.getSelectedItemPosition() + 1; // 1=Admin,2=Trabajador

        if (correo.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest request = new RegisterRequest(username, correo, password, idRol);

        // Logging HTTP
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        UsuarioService service = retrofit.create(UsuarioService.class);
        service.register(request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                    editCorreo.setText("");
                    editUsername.setText("");
                    editPassword.setText("");
                } else {
                    Toast.makeText(getContext(), "Error al registrar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}