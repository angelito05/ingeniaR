package com.example.ingenia.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ingenia.Model.LoginRequest;
import com.example.ingenia.Model.User;
import com.example.ingenia.R;
import com.example.ingenia.Util.SessionManager;
import com.example.ingenia.api.ApiConfig;
import com.example.ingenia.api.UsuarioService;
import com.example.ingenia.databinding.ActivityLoginBinding;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnEntrar.setOnClickListener(v -> {
            String user = binding.txtUsuario.getText().toString().trim();
            String password = binding.txtContra.getText().toString().trim();

            if (user.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Llena ambos campos", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest loginRequest = new LoginRequest(user, password);

            // Logging interceptor para depuración
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d("API_LOG", message));
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            UsuarioService usuarioService = retrofit.create(UsuarioService.class);

            usuarioService.login(loginRequest).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User usuario = response.body();

                        // Guardar id_usuario y username en SharedPreferences "CrediGoPrefs"
                        SessionManager sessionManager = new SessionManager(LoginActivity.this);
                        sessionManager.saveSession(usuario);

                        if (usuario.id_rol == 1) {
                            Intent intAdmin = new Intent(LoginActivity.this, admin.class);
                            intAdmin.putExtra("usuario", usuario.getUsername());
                            startActivity(intAdmin);
                            finish();
                        } else if (usuario.id_rol == 2) {
                            Intent intTrab = new Intent(LoginActivity.this, InicioActivity.class);
                            intTrab.putExtra("usuario", usuario.getUsername());
                            startActivity(intTrab);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Rol no reconocido", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        try {
                            if (response.errorBody() != null) {
                                String errorMsg = response.errorBody().string();
                                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                Log.e("API_ERROR", "Mensaje del servidor: " + errorMsg);
                            } else {
                                Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Toast.makeText(LoginActivity.this, "Error al leer el mensaje del servidor", Toast.LENGTH_SHORT).show();
                            Log.e("API_ERROR", "Excepción: ", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("API_ERROR", "Falló la petición: ", t);
                }
            });
        });


        final boolean[] esVisible = {false};

        binding.txtContra.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.txtContra.getRight()
                        - binding.txtContra.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {

                    esVisible[0] = !esVisible[0];

                    if (esVisible[0]) {
                        binding.txtContra.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        binding.txtContra.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.sharp_visibility, 0);
                    } else {
                        binding.txtContra.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        binding.txtContra.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.sharp_visibility_off, 0);
                    }

                    binding.txtContra.setSelection(binding.txtContra.getText().length());
                    return true;
                }
            }
            return false;
        });
    }
}
