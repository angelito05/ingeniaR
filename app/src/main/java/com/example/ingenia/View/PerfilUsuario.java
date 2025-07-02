package com.example.ingenia.View;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.ingenia.R;
import com.example.ingenia.View.LoginActivity;

public class PerfilUsuario extends Fragment {

    public PerfilUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvCorreo = view.findViewById(R.id.tvCorreo);
        TextView tvRol = view.findViewById(R.id.tvRol);
        TextView tvEstado = view.findViewById(R.id.tvEstado);
        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarS);

        // Obtener SharedPreferences sesión guardada
        SharedPreferences prefs = requireActivity().getSharedPreferences("CrediGoPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Obtener datos guardados con valores por defecto
        String username = prefs.getString("username", "Usuario");
        String correo = prefs.getString("correo", "correo@ejemplo.com");
        int idRol = prefs.getInt("id_rol", 2);
        boolean activo = prefs.getBoolean("activo", true);

        String rolTexto = (idRol == 1) ? "Administrador" : "Empleado";
        String estadoTexto = activo ? "Activo" : "Inactivo";

        // Mostrar datos
        tvUsername.setText("Usuario: " + username);
        tvCorreo.setText("Correo: " + correo);
        tvRol.setText("Rol: " + rolTexto);
        tvEstado.setText("Estado: " + estadoTexto);

        // Botón cerrar sesión: borrar sesión y regresar a Login
        btnCerrarSesion.setOnClickListener(v -> {
            editor.clear();
            editor.apply();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}
