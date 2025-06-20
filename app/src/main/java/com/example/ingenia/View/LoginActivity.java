package com.example.ingenia.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ingenia.R;
import com.example.ingenia.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    String user1 = "admin";
    String user2 = "Angelito";
    String user1pas = "admin123";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnEntrar.setOnClickListener(v -> {
            //obtener valores de usuario y password
            String user= binding.txtUsuario.getText().toString();
            String password = binding.txtContra.getText().toString();

            if(user.equals(user1) || user.equals(user2)){
                if(password.equals(user1pas)){
                    Intent int1 = new Intent(getApplicationContext(), InicioActivity.class);
                    int1.putExtra("usuario",user);
                    startActivity(int1);
                } else {
                    Toast.makeText(LoginActivity.this, "Contraseña Incorrecta", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Usuario Incorrecto", Toast.LENGTH_SHORT).show();
            }
        });

        final boolean[] esVisible = {false};

        binding.txtContra.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2; // 0=left, 1=top, 2=right, 3=bottom

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.txtContra.getRight()
                        - binding.txtContra.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {

                    esVisible[0] = !esVisible[0];  // alternar estado

                    if (esVisible[0]) {
                        // Mostrar contraseña
                        binding.txtContra.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        binding.txtContra.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.sharp_visibility, 0);
                    } else {
                        // Ocultar contraseña
                        binding.txtContra.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        binding.txtContra.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.sharp_visibility_off, 0);
                    }

                    // Mantener cursor al final
                    binding.txtContra.setSelection(binding.txtContra.getText().length());
                    return true;
                }
            }

            return false;
        });
        binding.txtregis.setOnClickListener(
                v -> startActivity(new Intent(this, MainActivity.class))
        );
        
    }
}