package com.example.ingenia.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ingenia.Model.Solicitud;
import androidx.core.content.ContextCompat;
import com.example.ingenia.Model.SolicitudRepository;
import com.example.ingenia.R;

public class CrearSolicitudFragment extends Fragment {

    private EditText inputNombre, inputApellidoPaterno, inputApellidoMaterno;
    private EditText inputCurp, inputClaveElector, inputFechaNacimiento, inputGenero;
    private EditText inputColonia, inputCalle, inputCiudad, inputEstado, inputCp;
    private TextView labelCurpValida, labelIneValida;
    private Button btnEscanear, btnValidar, btnCrear;

    private boolean datosValidados = false;

    public CrearSolicitudFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crear_solicitud, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Vincular vistas
        inputNombre = view.findViewById(R.id.input_nombre);
        inputApellidoPaterno = view.findViewById(R.id.input_apellido_paterno);
        inputApellidoMaterno = view.findViewById(R.id.input_apellido_materno);
        inputCurp = view.findViewById(R.id.input_curp);
        inputClaveElector = view.findViewById(R.id.input_clave_elector);
        inputFechaNacimiento = view.findViewById(R.id.input_fecha_nacimiento);
        inputGenero = view.findViewById(R.id.input_genero);
        inputColonia = view.findViewById(R.id.input_colonia);
        inputCalle = view.findViewById(R.id.input_calle);
        inputCiudad = view.findViewById(R.id.input_ciudad);
        inputEstado = view.findViewById(R.id.input_estado);
        inputCp = view.findViewById(R.id.input_cp);
        labelCurpValida = view.findViewById(R.id.label_curp_valida);
        labelIneValida = view.findViewById(R.id.label_ine_valida);
        btnEscanear = view.findViewById(R.id.btn_escanear_ine);
        btnValidar = view.findViewById(R.id.btn_validar_datos);
        btnCrear = view.findViewById(R.id.btn_crear_solicitud);

        btnEscanear.setOnClickListener(v -> simularLlenadoOCR());
        btnValidar.setOnClickListener(v -> simularValidacionDatos());

        btnCrear.setOnClickListener(v -> {
            if (!datosValidados) {
                Toast.makeText(getContext(), "Primero valida los datos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Combinar nombre completo
            String nombreCompleto = inputNombre.getText().toString().trim() + " " +
                    inputApellidoPaterno.getText().toString().trim() + " " +
                    inputApellidoMaterno.getText().toString().trim();

            String detalles = "CURP: " + inputCurp.getText().toString().trim() +
                    "\nINE: " + inputClaveElector.getText().toString().trim() +
                    "\nDirección: " + inputCalle.getText().toString().trim() + ", " +
                    inputColonia.getText().toString().trim() + ", " +
                    inputCiudad.getText().toString().trim() + ", " +
                    inputEstado.getText().toString().trim() +
                    "\nCP: " + inputCp.getText().toString().trim();

            // Nombre del trabajador (puedes reemplazarlo luego con sesión real)
            String nombreTrabajador = "Ronald Leyva";

            Solicitud nueva = new Solicitud(
                    nombreCompleto,
                    detalles,
                    Solicitud.Estado.PENDIENTE,
                    nombreTrabajador
            );

            SolicitudRepository.listaSolicitudes.add(nueva);

            Toast.makeText(getContext(), "Solicitud creada correctamente", Toast.LENGTH_SHORT).show();

            limpiarFormulario();
        });

        btnCrear.setEnabled(false);
    }

    private void simularLlenadoOCR() {
        inputNombre.setText("Antonio");
        inputApellidoPaterno.setText("Bello");
        inputApellidoMaterno.setText("Ramírez");
        inputCurp.setText("BERA920101HDFLRS05");
        inputClaveElector.setText("BELR920101");
        inputFechaNacimiento.setText("01/01/1992");
        inputGenero.setText("Hombre");
        inputColonia.setText("Centro");
        inputCalle.setText("Av. Reforma");
        inputCiudad.setText("CDMX");
        inputEstado.setText("Ciudad de México");
        inputCp.setText("06000");

        Toast.makeText(getContext(), "Datos escaneados (simulado)", Toast.LENGTH_SHORT).show();
    }

    private void simularValidacionDatos() {
        labelCurpValida.setText("CURP: VÁLIDA");
        labelCurpValida.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));

        labelIneValida.setText("INE: VÁLIDA");
        labelIneValida.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));

        datosValidados = true;
        btnCrear.setEnabled(true);

        Toast.makeText(getContext(), "Datos validados correctamente", Toast.LENGTH_SHORT).show();
    }

    private void limpiarFormulario() {
        inputNombre.setText("");
        inputApellidoPaterno.setText("");
        inputApellidoMaterno.setText("");
        inputCurp.setText("");
        inputClaveElector.setText("");
        inputFechaNacimiento.setText("");
        inputGenero.setText("");
        inputColonia.setText("");
        inputCalle.setText("");
        inputCiudad.setText("");
        inputEstado.setText("");
        inputCp.setText("");

        labelCurpValida.setText("");
        labelIneValida.setText("");
        btnCrear.setEnabled(false);
        datosValidados = false;
    }
}
