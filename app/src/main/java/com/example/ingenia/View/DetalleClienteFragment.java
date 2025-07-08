package com.example.ingenia.View;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ingenia.Model.Cliente;
import com.example.ingenia.R;
import com.example.ingenia.api.ApiConfig;
import com.example.ingenia.api.UsuarioService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetalleClienteFragment extends Fragment {

    private EditText etNombre, etApellidoPaterno, etApellidoMaterno, etCurp, etClaveElector;
    private EditText etCalle, etColonia, etCiudad, etEstado, etCodigoPostal, etGenero;
    private Button btnGuardar, btnEliminar, btnValidar;
    private int idCliente;

    private UsuarioService service;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detalle_cliente, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencias a todos los EditText
        etNombre = view.findViewById(R.id.etNombre);
        etApellidoPaterno = view.findViewById(R.id.etApellidoPaterno);
        etApellidoMaterno = view.findViewById(R.id.etApellidoMaterno);
        etCurp = view.findViewById(R.id.etCurp);
        etClaveElector = view.findViewById(R.id.etClaveElector);

        etCalle = view.findViewById(R.id.etCalle);
        etColonia = view.findViewById(R.id.etColonia);
        etCiudad = view.findViewById(R.id.etCiudad);
        etEstado = view.findViewById(R.id.etEstado);
        etCodigoPostal = view.findViewById(R.id.etCodigoPostal);
        etGenero = view.findViewById(R.id.etGenero);

        btnGuardar = view.findViewById(R.id.btnGuardar);
        btnEliminar = view.findViewById(R.id.btnEliminar);
        btnValidar = view.findViewById(R.id.btnValidar);

        Bundle args = getArguments();
        if (args == null || !args.containsKey("id_cliente")) {
            Toast.makeText(getContext(), "ID de cliente inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        idCliente = args.getInt("id_cliente");

        // Retrofit setup
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(UsuarioService.class);

        cargarDatosCliente();

        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnEliminar.setOnClickListener(v -> eliminarCliente());
        btnValidar.setOnClickListener(v -> redirigirAValidacion());
    }

    private void cargarDatosCliente() {
        service.getClientePorId(idCliente).enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Cliente cliente = response.body();
                    etNombre.setText(cliente.nombre);
                    etApellidoPaterno.setText(cliente.apellido_paterno);
                    etApellidoMaterno.setText(cliente.apellido_materno);
                    etCurp.setText(cliente.curp);
                    etClaveElector.setText(cliente.clave_elector);

                    etCalle.setText(cliente.calle);
                    etColonia.setText(cliente.colonia);
                    etCiudad.setText(cliente.ciudad);
                    etEstado.setText(cliente.estado);
                    etCodigoPostal.setText(cliente.codigo_postal);
                    etGenero.setText(cliente.genero);
                } else {
                    Toast.makeText(getContext(), "No se pudo cargar cliente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarCambios() {
        Cliente actualizado = new Cliente();
        actualizado.idCliente = idCliente;
        actualizado.nombre = etNombre.getText().toString();
        actualizado.apellido_paterno = etApellidoPaterno.getText().toString();
        actualizado.apellido_materno = etApellidoMaterno.getText().toString();
        actualizado.curp = etCurp.getText().toString();
        actualizado.clave_elector = etClaveElector.getText().toString();

        actualizado.calle = etCalle.getText().toString();
        actualizado.colonia = etColonia.getText().toString();
        actualizado.ciudad = etCiudad.getText().toString();
        actualizado.estado = etEstado.getText().toString();
        actualizado.codigo_postal = etCodigoPostal.getText().toString();
        actualizado.genero = etGenero.getText().toString();

        service.actualizarCliente(idCliente, actualizado).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getContext(), "Cliente actualizado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al actualizar cliente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error al actualizar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarCliente() {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Cliente")
                .setMessage("¿Estás seguro que deseas eliminar este cliente?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    service.eliminarCliente(idCliente).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Cliente eliminado", Toast.LENGTH_SHORT).show();
                                requireActivity().getSupportFragmentManager().popBackStack();
                            } else if (response.code() == 401) {
                                Toast.makeText(getContext(), "No tienes permiso para eliminar este cliente", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), "Error al eliminar cliente", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void redirigirAValidacion() {
        // Cambia esto por el fragmento que uses para validar con OCR
        CrearSolicitudFragment fragment = new CrearSolicitudFragment();

        Bundle args = new Bundle();
        args.putInt("id_cliente", idCliente);
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }
}
