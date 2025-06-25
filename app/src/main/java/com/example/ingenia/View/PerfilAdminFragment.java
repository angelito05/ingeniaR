package com.example.ingenia.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ingenia.R;

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

        // SharedPreferences para datos persistentes
        SharedPreferences prefs = requireActivity().getSharedPreferences("perfil_admin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Datos simulados por defecto
        String nombre = prefs.getString("nombre", "");
        String apellidoPaterno = prefs.getString("apellidoPaterno", "");
        String apellidoMaterno = prefs.getString("apellidoMaterno", "");
        String username = prefs.getString("username", "admin123");
        String correo = prefs.getString("correo", "oscarj@example.com");
        int idRol = prefs.getInt("rol", 1); // 1 = Admin
        boolean activo = prefs.getBoolean("activo", true);

        // Preparar texto para mostrar
        String nombreCompleto = nombre + " " + apellidoPaterno + " " + apellidoMaterno;
        String rolTexto = (idRol == 1) ? "Administrador" : "Otro rol";
        String estadoTexto = activo ? "Activo" : "Inactivo";

        // Mostrar en pantalla
        tvNombreCompleto.setText(nombreCompleto);
        tvUsername.setText("Username: " + username);
        tvCorreo.setText("Correo: " + correo);
        tvRol.setText("Rol: " + rolTexto);
        tvEstado.setText("Estado: " + estadoTexto);

        // Botón Editar Perfil
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
                            editor.putString("nombre", nuevoNombre); // Guarda solo el nombre completo
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
