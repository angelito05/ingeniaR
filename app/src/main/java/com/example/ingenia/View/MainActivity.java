package com.example.ingenia.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ingenia.Controller.MainController;
import com.example.ingenia.Model.Usuario;
import com.example.ingenia.Model.Validator;
import com.example.ingenia.R;
import com.example.ingenia.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mostrarMensajeBienvenida();

        binding.enviar.setEnabled(false);

        binding.textnombre.addTextChangedListener(watcher);
        binding.textappa.addTextChangedListener(watcher);
        binding.textapma.addTextChangedListener(watcher);
        binding.textcorreo.addTextChangedListener(watcher);
        binding.textcon.addTextChangedListener(watcher);

        // A nivel de clase o dentro de onCreate():
        final boolean[] esVisible = {false};

        binding.textcon.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2; // 0=left, 1=top, 2=right, 3=bottom

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.textcon.getRight()
                        - binding.textcon.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {

                    esVisible[0] = !esVisible[0];  // alternar estado

                    if (esVisible[0]) {
                        // Mostrar contraseña
                        binding.textcon.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        binding.textcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.sharp_visibility, 0);
                    } else {
                        // Ocultar contraseña
                        binding.textcon.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        binding.textcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.sharp_visibility_off, 0);
                    }

                    // Mantener cursor al final
                    binding.textcon.setSelection(binding.textcon.getText().length());
                    return true;
                }
            }

            return false;
        });

        binding.enviar.setOnClickListener(v -> {
            Usuario usuario = MainController.construirUsuario(
                    binding.textnombre,
                    binding.textappa,
                    binding.textapma,
                    binding.textusername,
                    binding.textcorreo,
                    binding.textcon
            );

            mostrarResumen(usuario);
        });
        binding.txtvol.setOnClickListener(
                v -> startActivity(new Intent(this, LoginActivity.class))
        );

    }
    private void mostrarMensajeBienvenida() {
        String nombreUsuario = getIntent().getStringExtra("usuario");
        if (nombreUsuario != null) {
            binding.mensaje.setText("Bienvenido " + nombreUsuario);
        }
    }
    private void mostrarResumen(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Datos de Registro")
                .setMessage(usuario.getResumen())
                .setPositiveButton("OK", (dialog, which) ->
                        Toast.makeText(builder.getContext(),"Gracias por registrarte", Toast.LENGTH_SHORT).show())
                .show();
    }

    private final android.text.TextWatcher watcher = new android.text.TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(android.text.Editable s) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String contrasena = s.toString();
            String[] sugerencias = Validator.sugerenciasContrasena(contrasena);

            if (sugerencias.length == 0) {
                binding.textcon.setError(null); // Contraseña válida
            } else {
                StringBuilder mensaje = new StringBuilder("Recomendaciones:\n");
                for (String sugerencia : sugerencias) {
                    mensaje.append("• ").append(sugerencia).append("\n");
                }
                binding.textcon.setError(mensaje.toString().trim());
            }
            // También puedes validar si debe habilitarse el botón enviar
            boolean camposLlenos = MainController.validarFormulario(
                    binding.textnombre,
                    binding.textappa,
                    binding.textapma,
                    binding.textcorreo
            );
            boolean contraseñaSegura = Validator.esContrasenaSegura(contrasena);
            binding.enviar.setEnabled(camposLlenos && contraseñaSegura);
        }

    };
}