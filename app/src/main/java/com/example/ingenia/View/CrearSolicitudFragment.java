package com.example.ingenia.View;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ingenia.Model.ClienteRequest;
import com.example.ingenia.Model.Cliente;
import com.example.ingenia.R;
import com.example.ingenia.api.ApiConfig;
import com.example.ingenia.api.UsuarioService;

import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrearSolicitudFragment extends Fragment {

    private EditText inputNombre, inputApellidoPaterno, inputApellidoMaterno;
    private EditText inputCurp, inputClaveElector, inputFechaNacimiento, inputGenero;
    private EditText inputColonia, inputCalle, inputCiudad, inputEstado, inputCp;
    private TextView labelCurpValida, labelIneValida;
    private Button btnEscanear, btnValidar, btnSolicitar;

    private boolean datosValidados = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear_solicitud, container, false);

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
        btnSolicitar = view.findViewById(R.id.btn_crear_solicitud);

        inputFechaNacimiento.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(getContext(), (view1, y, m, d) -> {
                String fechaFormateada = String.format("%04d-%02d-%02d", y, m + 1, d);
                inputFechaNacimiento.setText(fechaFormateada);
            }, year, month, day);

            picker.show();
        });

        btnEscanear.setOnClickListener(v -> simularLlenadoOCR());
        btnValidar.setOnClickListener(v -> simularValidacionDatos());
        btnSolicitar.setOnClickListener(v -> crearCliente());

        btnSolicitar.setEnabled(false);
        return view;
    }

    private void crearCliente() {
        if (!datosValidados) {
            Toast.makeText(getContext(), "Primero valida los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = inputNombre.getText().toString().trim();
        String apellidoP = inputApellidoPaterno.getText().toString().trim();
        String apellidoM = inputApellidoMaterno.getText().toString().trim();
        String curp = inputCurp.getText().toString().trim();
        String claveElector = inputClaveElector.getText().toString().trim();
        String fechaNacimiento = inputFechaNacimiento.getText().toString().trim();
        String genero = inputGenero.getText().toString().trim();
        String calle = inputCalle.getText().toString().trim();
        String colonia = inputColonia.getText().toString().trim();
        String ciudad = inputCiudad.getText().toString().trim();
        String estado = inputEstado.getText().toString().trim();
        String codigoPostal = inputCp.getText().toString().trim();

        if (nombre.isEmpty() || curp.isEmpty()) {
            Toast.makeText(getContext(), "Nombre y CURP son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener id_usuario desde SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CrediGoPrefs", Context.MODE_PRIVATE);
        int idUsuario = sharedPreferences.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(getContext(), "Error: no se encontró el usuario logeado", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ID_USUARIO_LOGEADO", "ID: " + idUsuario);

        ClienteRequest request = new ClienteRequest(
                nombre,
                apellidoP,
                apellidoM,
                curp,
                claveElector,
                fechaNacimiento,
                genero,
                calle,
                colonia,
                ciudad,
                estado,
                codigoPostal,
                idUsuario
        );

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        UsuarioService service = retrofit.create(UsuarioService.class);

        service.crearCliente(request).enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int idCliente = response.body().idCliente;

                    Toast.makeText(getContext(), "Cliente creado con ID: " + idCliente, Toast.LENGTH_LONG).show();
                    limpiarFormulario();
                    datosValidados = false;
                    btnSolicitar.setEnabled(false);

                    // Redirigir a fragmento final con el ID del cliente
                    SolicitudFinalFragment solicitudFinalFragment = new SolicitudFinalFragment();
                    Bundle args = new Bundle();
                    args.putInt("id_cliente", idCliente);
                    solicitudFinalFragment.setArguments(args);

                    FragmentTransaction transaction = requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction();

                    transaction.replace(R.id.container_fragment, solicitudFinalFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(getContext(), "Error al crear cliente: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                Toast.makeText(getContext(), "Falla en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void simularLlenadoOCR() {
        inputNombre.setText("Karen");
        inputApellidoPaterno.setText("Bello");
        inputApellidoMaterno.setText("Ramírez");
        inputCurp.setText("BERA920101HDFLRS05");
        inputClaveElector.setText("BELR920101");
        inputFechaNacimiento.setText("1992-01-01");
        inputGenero.setText("Femenino");
        inputColonia.setText("Norte");
        inputCalle.setText("Av. Insurgentes");
        inputCiudad.setText("CDMX");
        inputEstado.setText("Ciudad de México");
        inputCp.setText("06000");

        Toast.makeText(getContext(), "Datos escaneados (simulado)", Toast.LENGTH_SHORT).show();
    }

    private void simularValidacionDatos() {
        labelCurpValida.setText("CURP: VÁLIDA");
        labelCurpValida.setTextColor(requireContext().getColor(android.R.color.holo_green_dark));

        labelIneValida.setText("INE: VÁLIDA");
        labelIneValida.setTextColor(requireContext().getColor(android.R.color.holo_green_dark));

        datosValidados = true;
        btnSolicitar.setEnabled(true);

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
    }
}
