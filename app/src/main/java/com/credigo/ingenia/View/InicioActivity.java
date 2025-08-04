package com.credigo.ingenia.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.credigo.ingenia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Obtener usuario desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("CrediGoPrefs", MODE_PRIVATE);
        String nombreUsuario = prefs.getString("username", null);

        // Verificar sesión activa
        if (nombreUsuario == null) {
            // No hay sesión, redirigir a login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Fragmento inicial
        if (savedInstanceState == null) {
            loadFragment(new RendimientoFragment());
        }

        // Listener de navegación
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.solicitudes) {
                selectedFragment = new RendimientoFragment();
            } else if (id == R.id.clientes) {
                selectedFragment = new ClientesFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new PerfilUsuario();
            } else if (id == R.id.nav_register) {
                selectedFragment = new CrearSolicitudFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_fragment, fragment)
                .commit();
    }
}
