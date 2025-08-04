package com.credigo.ingenia.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.credigo.ingenia.Model.User;
import com.credigo.ingenia.Model.UsuarioActualizarDTO;
import com.credigo.ingenia.R;
import com.credigo.ingenia.Util.SessionManager;
import com.credigo.ingenia.api.ApiConfig;
import com.credigo.ingenia.api.UsuarioService;
import com.credigo.ingenia.databinding.FragmentPerfilUsuarioBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilUsuario extends Fragment {
    FragmentPerfilUsuarioBinding binding;

    public PerfilUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPerfilUsuarioBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        SessionManager sessionManager = new SessionManager(requireContext());

        // Leer datos locales guardados
        String username = sessionManager.getUsername();
        String correo = sessionManager.getCorreo();
        String rolTexto = sessionManager.getIdRol() == 1 ? "Administrador" : "Empleado";
        String estadoTexto = sessionManager.isActivo() ? "Activo" : "Inactivo";

        // Mostrar en pantalla
        binding.tvUsername.setText("Username: " + username);
        binding.tvCorreo.setText("Correo: " + correo);
        binding.tvRol.setText("Rol: " + rolTexto);
        binding.tvEstado.setText("Estado: " + estadoTexto);

        binding.btnEditar.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_editar_perfil, null);
            EditText etNuevoNombre = dialogView.findViewById(R.id.etNuevoNombre);
            EditText etNuevaPassword = dialogView.findViewById(R.id.etNuevaPassword);

            etNuevoNombre.setText(sessionManager.getUsername()); // Muestra el username actual (puedes cambiar a nombre real)

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
                        int userId = sessionManager.getIdUsuario(); // Ya que ya tienes sessionManager

                        apiService.actualizarUsuario(userId, dto).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    User actualizado = response.body();

                                    binding.tvUsername.setText("Username: " + actualizado.getUsername());

                                    // Guardar los nuevos datos en la sesión
                                    sessionManager.saveSession(actualizado);

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



        // Botón cerrar sesión
        binding.btnCerrarSesion.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

}
// Editar perfil
            /*binding.btnEditar.setOnClickListener(v -> {
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_editar_perfil, null);
                EditText etNuevoNombre = dialogView.findViewById(R.id.etNuevoNombre);
                EditText etNuevaPassword = dialogView.findViewById(R.id.etNuevaPassword);

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

                            apiService.actualizarUsuario(userId, dto).enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        User actualizado = response.body();
                                        binding.tvUsername.setText("Username: " + actualizado.getUsername());
                                        Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();

                                        // Actualizar localmente
                                        sessionManager.saveSession(userId, actualizado.getUsername(), actualizado.getId_rol());
                                    } else {
                                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
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
            });*/