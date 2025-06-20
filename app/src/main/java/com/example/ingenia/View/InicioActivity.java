package com.example.ingenia.View;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.Menu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ingenia.Controller.MainController;
import com.example.ingenia.Model.Usuario;
import com.example.ingenia.R;
import com.example.ingenia.databinding.ActivityInicioBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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