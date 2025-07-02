package com.example.ingenia.View;

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

import com.example.ingenia.Model.User;
import com.example.ingenia.R;
import com.example.ingenia.api.ApiConfig;
import com.example.ingenia.api.UsuarioService;
import com.example.ingenia.databinding.FragmentPerfilUsuarioBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilUsuario extends Fragment {

    public PerfilUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        // Referencias a los elementos UI
        TextView tvNombreCompleto = view.findViewById(R.id.tvNombreCompleto);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvCorreo = view.findViewById(R.id.tvCorreo);
        TextView tvRol = view.findViewById(R.id.tvRol);
        TextView tvEstado = view.findViewById(R.id.tvEstado);
        Button btnEditarPerfil = view.findViewById(R.id.btnEditar);
        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarS);

        // Obtener ID del usuario desde SharedPreferences global de sesión
        SharedPreferences prefs = requireActivity().getSharedPreferences("credigo_session", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            UsuarioService apiService = ApiConfig.getRetrofit().create(UsuarioService.class);
            Call<User> call = apiService.ObtenerUsuario(userId);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();

                        String nombreCompleto = user.getUsername(); // Modifícalo si luego agregas nombre real
                        String rolTexto = user.getId_rol() == 1 ? "Administrador" : "Empleado";
                        String estadoTexto = user.isActivo() ? "Activo" : "Inactivo";

                        tvNombreCompleto.setText(nombreCompleto);
                        tvUsername.setText("Username: " + user.getUsername());
                        tvCorreo.setText("Correo: " + user.getCorreo());
                        tvRol.setText("Rol: " + rolTexto);
                        tvEstado.setText("Estado: " + estadoTexto);
                    } else {
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

        // Acción de editar perfil
      /* btnEditarPerfil.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_editar_perfil, null);
            EditText etNuevoNombre = dialogView.findViewById(R.id.etNuevoNombre);
            EditText etNuevaPassword = dialogView.findViewById(R.id.etNuevaPassword);

            etNuevoNombre.setText(nombreCompleto);

            new AlertDialog.Builder(getContext())
                    .setTitle("Editar Perfil")
                    .setView(dialogView)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String nuevoNombre = etNuevoNombre.getText().toString().trim();
                        String nuevaPass = etNuevaPassword.getText().toString().trim();

                        if (!nuevoNombre.isEmpty()) {
                            tvNombreCompleto.setText(nuevoNombre);
                            // Guardar nombre completo como "nombre"
                            editor.putString("nombre", nuevoNombre); // O separarlo si quieres por campos
                            editor.apply();
                        }

                        if (!nuevaPass.isEmpty()) {
                            if (nuevaPass.length() < 6) {
                                Toast.makeText(getContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                            } else {
                                editor.putString("password", nuevaPass);
                                editor.apply();
                                Toast.makeText(getContext(), "Contraseña cambiada", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });*/

        // Cerrar sesión y limpiar stack
        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }





}