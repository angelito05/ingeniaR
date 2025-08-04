package com.credigo.ingenia.View;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.credigo.ingenia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class admin extends AppCompatActivity {

    private int idUsuario;
    private String username;
    private int idRol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        // Leer datos de sesión guardados en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("CrediGoPrefs", MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);
        username = prefs.getString("username", null);
        idRol = prefs.getInt("id_rol", -1);

       if (idUsuario == -1 || username == null || idRol == -1) {
            Toast.makeText(this, "Sesión no iniciada o expirada, por favor inicia sesión nuevamente", Toast.LENGTH_LONG).show();
            finish(); // o redirige al login
            return;
        }

        // Ajuste padding para sistema Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadFragment(new SolicitudesFragment());
        }


        // Listener para seleccionar menú
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.nav_stats) {
                selectedFragment = new SolicitudesFragment();
            } else if (id == R.id.nav_empleados) {
                selectedFragment = new Empleados_Fragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new PerfilAdminFragment();
            } else if (id == R.id.nav_register) {
                selectedFragment = new CrearFragment();
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
