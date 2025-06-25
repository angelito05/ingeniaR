package com.example.ingenia.View;

import android.app.AlertDialog;
import android.content.Intent;
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

    private FragmentPerfilUsuarioBinding binding;

    public PerfilUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPerfilUsuarioBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnEdtrPerfil.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_editar_perfil, null);

            TextView tvNombreCompleto = view.findViewById(R.id.tvNombreCompleto);
            TextView tvUsername = view.findViewById(R.id.tvUsername);
            TextView tvCorreo = view.findViewById(R.id.tvCorreo);
            TextView tvRol = view.findViewById(R.id.tvRol);
            TextView tvEstado = view.findViewById(R.id.tvEstado);
            Button btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
            Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

            // 2. Datos simulados del administrador
            String nombre = "Oscar";
            String apellidoPaterno = "J.";
            String apellidoMaterno = "Ortega";
            String username = "admin123";
            String correo = "oscarj@example.com";
            int idRol = 1; // Asumimos que 1 = Admin
            boolean activo = true;

            // 3. Asignamos los datos a los TextView
            String[] nombreCompleto = {nombre + " " + apellidoPaterno + " " + apellidoMaterno};
            String rolTexto = (idRol == 1) ? "Administrador" : "Otro rol";
            String estadoTexto = activo ? "Activo" : "Inactivo";

            tvNombreCompleto.setText(nombreCompleto[0]);
            tvUsername.setText("Username: " + username);
            tvCorreo.setText("Correo: " + correo);
            tvRol.setText("Rol: " + rolTexto);
            tvEstado.setText("Estado: " + estadoTexto);

            EditText etNuevaPassword = dialogView.findViewById(R.id.etNuevaPassword);
            EditText etNuevoNombre = dialogView.findViewById(R.id.etNuevoNombre);

            etNuevoNombre.setText(nombreCompleto[0]);

            new AlertDialog.Builder(getContext())
                    .setTitle("Editar Perfil")
                    .setView(dialogView)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        // Lógica para guardar los cambios
                        String nuevoNombre = etNuevoNombre.getText().toString().trim();
                        String nuevaPass = etNuevaPassword.getText().toString().trim();

                        if (!nuevoNombre.isEmpty()) {
                            nombreCompleto[0] = nuevoNombre;
                            tvNombreCompleto.setText(nuevoNombre);
                        }

                        if (!nuevaPass.isEmpty()) {
                            if (nuevaPass.length() < 6) {
                                Toast.makeText(getContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Contraseña cambiada", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        binding.btnCerrarS.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });
        return view;
    }

}