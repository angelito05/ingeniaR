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

import com.example.ingenia.R;
import com.example.ingenia.databinding.FragmentPerfilUsuarioBinding;

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

        SharedPreferences prefs = requireActivity().getSharedPreferences("perfil_usuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Recuperar SIEMPRE los datos más recientes
        String nombre = prefs.getString("nombre", "");
        String apellidoPaterno = prefs.getString("apellidoPaterno", "");
        String apellidoMaterno = prefs.getString("apellidoMaterno", "");
        String username = prefs.getString("username", "Ronald123");
        String correo = prefs.getString("correo", "ronald@example.com");
        int idRol = prefs.getInt("rol", 2);
        boolean activo = prefs.getBoolean("activo", true);

        String nombreCompleto = nombre + " " + apellidoPaterno + " " + apellidoMaterno;
        String rolTexto = (idRol == 1) ? "Administrador" : "Empleado";
        String estadoTexto = activo ? "Activo" : "Inactivo";

        // Mostrar datos
        tvNombreCompleto.setText(nombreCompleto);
        tvUsername.setText("Username: " + username);
        tvCorreo.setText("Correo: " + correo);
        tvRol.setText("Rol: " + rolTexto);
        tvEstado.setText("Estado: " + estadoTexto);

        // Acción de editar perfil
        btnEditarPerfil.setOnClickListener(v -> {
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
        });

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