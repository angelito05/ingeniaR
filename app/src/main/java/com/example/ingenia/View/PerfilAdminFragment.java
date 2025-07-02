package com.example.ingenia.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ingenia.Model.User;
import com.example.ingenia.Model.UsuarioActualizarDTO;
import com.example.ingenia.R;
import com.example.ingenia.api.ApiConfig;
import com.example.ingenia.api.UsuarioService;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilAdminFragment extends Fragment {

    public PerfilAdminFragment() {
        // Constructor vacío obligatorio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_admin, container, false);

        // Referencias UI
        TextView tvNombreCompleto = view.findViewById(R.id.tvNombreCompleto);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvCorreo = view.findViewById(R.id.tvCorreo);
        TextView tvRol = view.findViewById(R.id.tvRol);
        TextView tvEstado = view.findViewById(R.id.tvEstado);
        Button btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        // Obtener ID del usuario desde SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("credigo_session", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            UsuarioService apiService = ApiConfig.getRetrofit().create(UsuarioService.class);
            Call<User> call = apiService.ObtenerUsuario(userId);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Log.d("PerfilFragment", "Código HTTP: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        Log.d("PerfilFragment", "Usuario recibido: " + new Gson().toJson(user));

                        String nombreCompleto = user.getUsername();
                        String rolTexto = user.getId_rol() == 1 ? "Administrador" : "Trabajador";
                        String estadoTexto = user.isActivo() ? "Activo" : "Inactivo";

                        tvNombreCompleto.setText(nombreCompleto);
                        tvUsername.setText("Username: " + user.getUsername());
                        tvCorreo.setText("Correo: " + user.getCorreo());
                        tvRol.setText("Rol: " + rolTexto);
                        tvEstado.setText("Estado: " + estadoTexto);
                    } else {
                        try {
                            Log.e("PerfilFragment", "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), "Error al cargar perfil", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Sesión no encontrada", Toast.LENGTH_SHORT).show();
        }

        // Botón Editar Perfil
        btnEditarPerfil.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_editar_perfil, null);
            EditText etNuevoNombre = dialogView.findViewById(R.id.etNuevoNombre);
            EditText etNuevaPassword = dialogView.findViewById(R.id.etNuevaPassword);

            etNuevoNombre.setText(tvNombreCompleto.getText().toString());

            new AlertDialog.Builder(getContext())
                    .setTitle("Editar Perfil")
                    .setView(dialogView)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String nuevoNombre = etNuevoNombre.getText().toString().trim();
                        String nuevaPass = etNuevaPassword.getText().toString().trim();

                        if (nuevoNombre.isEmpty() && nuevaPass.isEmpty()) {
                            Toast.makeText(getContext(), "No hay cambios que guardar", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        UsuarioActualizarDTO dto = new UsuarioActualizarDTO(
                                !nuevoNombre.isEmpty() ? nuevoNombre : null,
                                !nuevaPass.isEmpty() ? nuevaPass : null
                        );

                        UsuarioService apiService = ApiConfig.getRetrofit().create(UsuarioService.class);
                        Call<User> call = apiService.actualizarUsuario(userId, dto);

                        call.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful()) {
                                    User actualizado = response.body();
                                    tvNombreCompleto.setText(actualizado.getUsername());
                                    tvUsername.setText("Username: " + actualizado.getUsername());
                                    Toast.makeText(getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });


        // Botón Cerrar Sesión
       btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }



}
