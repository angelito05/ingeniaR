package com.example.ingenia.View;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ingenia.databinding.ActivityInicioBinding;

public class InicioActivity extends AppCompatActivity {
    ActivityInicioBinding  binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInicioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mostrarMensajeBienvenida();

    }

   private void mostrarMensajeBienvenida() {
        String nombreUsuario = getIntent().getStringExtra("usuario");
        if (nombreUsuario != null) {
            binding.bienvenida.setText("Bienvenido " + nombreUsuario);
        }
    }

}