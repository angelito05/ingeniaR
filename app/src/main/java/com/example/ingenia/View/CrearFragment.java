package com.example.ingenia.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ingenia.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CrearFragment extends Fragment {

    private TextInputEditText editNombre, editApellidoPaterno, editApellidoMaterno;
    private TextInputEditText editCorreo, editUsername, editPassword;
    private MaterialButton btnRegistrar;

    public CrearFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear, container, false);

        // Referencias a campos
        editNombre = view.findViewById(R.id.editNombre);
        editApellidoPaterno = view.findViewById(R.id.editApellidoPaterno);
        editApellidoMaterno = view.findViewById(R.id.editApellidoMaterno);
        editCorreo = view.findViewById(R.id.editCorreo);
        editUsername = view.findViewById(R.id.editUsername);
        editPassword = view.findViewById(R.id.editPassword);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);

        // Acción del botón
        btnRegistrar.setOnClickListener(v -> registrarUsuario());

        return view;
    }

    private void registrarUsuario() {
        // Obtener valores
        String nombre = editNombre.getText().toString().trim();
        String apellidoPaterno = editApellidoPaterno.getText().toString().trim();
        String apellidoMaterno = editApellidoMaterno.getText().toString().trim();
        String correo = editCorreo.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Validación
        if (nombre.isEmpty() || apellidoPaterno.isEmpty() || apellidoMaterno.isEmpty() ||
                correo.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Aquí va la lógica para enviar los datos (API, BD, etc.)
        Toast.makeText(getContext(), "Trabajador registrado exitosamente", Toast.LENGTH_SHORT).show();

        limpiarCampos();
    }

    private void limpiarCampos() {
        editNombre.setText("");
        editApellidoPaterno.setText("");
        editApellidoMaterno.setText("");
        editCorreo.setText("");
        editUsername.setText("");
        editPassword.setText("");
    }
}
