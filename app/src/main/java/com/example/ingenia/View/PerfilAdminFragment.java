package com.example.ingenia.View;

import android.app.AlertDialog;
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflamos la vista
        View view = inflater.inflate(R.layout.fragment_perfil_admin, container, false);

        // 1. Obtenemos referencias a los TextViews
        TextView tvNombreCompleto = view.findViewById(R.id.tvNombreCompleto);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvCorreo = view.findViewById(R.id.tvCorreo);
        TextView tvRol = view.findViewById(R.id.tvRol);
        TextView tvEstado = view.findViewById(R.id.tvEstado);
        Button btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);

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

        // 4. Configurar botón para editar perfil
        btnEditarPerfil.setOnClickListener(v -> {
            // Inflar el layout del diálogo
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_editar_perfil, null);

            EditText etNuevoNombre = dialogView.findViewById(R.id.etNuevoNombre);
            EditText etNuevaPassword = dialogView.findViewById(R.id.etNuevaPassword);

            // Prellenar nombre
            etNuevoNombre.setText(nombreCompleto[0]);

            new AlertDialog.Builder(getContext())
                    .setTitle("Editar Perfil")
                    .setView(dialogView)
                    .setPositiveButton("Guardar", (dialog, which) -> {
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

        return view;
    }
}
