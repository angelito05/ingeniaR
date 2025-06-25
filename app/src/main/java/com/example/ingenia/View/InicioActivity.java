package com.example.ingenia.View;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ingenia.R;
import com.example.ingenia.View.CrearSolicitudFragment;
import com.example.ingenia.View.RendimientoFragment;
import com.example.ingenia.View.PerfilUsuario;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.TextView;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Mostrar mensaje de bienvenida
        TextView bienvenidaText = findViewById(R.id.bienvenida);
        String nombreUsuario = getIntent().getStringExtra("usuario");
        if (nombreUsuario != null) {
            bienvenidaText.setText("Bienvenido " + nombreUsuario);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Fragmento inicial
        loadFragment(new RendimientoFragment());

        // Listener de navegación
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_stats) {
                selectedFragment = new RendimientoFragment();
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
