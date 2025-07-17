package com.example.ingenia.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ingenia.Model.ClienteRequest;
import com.example.ingenia.Model.Cliente;
import com.example.ingenia.Model.OcrResponse;
import com.example.ingenia.R;
import com.example.ingenia.api.ApiConfig;
import com.example.ingenia.api.UsuarioService;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executor;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrearSolicitudFragment extends Fragment {

    // Vistas
    private EditText inputNombre, inputApellidoPaterno, inputApellidoMaterno;
    private EditText inputCurp, inputClaveElector, inputFechaNacimiento, inputGenero;
    private EditText inputDomicilio, inputCiudad, inputEstado, inputCp;
    private TextView labelCurpValida, labelIneValida;
    private Button btnEscanear, btnValidar, btnSolicitar;
    private ImageView imagenPreviewINE;
    private PreviewView previewView;
    private Button btnCapture;
    private ImageButton btnFlashToggle;
    private RelativeLayout cameraContainer;
    private ImageView ineFrame;

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private Uri photoUri;
    private File photoFile;
    private boolean datosValidados = false;
    private boolean modoSoloLectura = false;
    private Executor cameraExecutor;
    private boolean isCameraActive = false;
    private boolean isFlashEnabled = true;

    // Variables para la cámara
    private ImageCapture imageCapture;



    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crear_solicitud, container, false);

        // Referenciar vistas
        inputNombre = view.findViewById(R.id.input_nombre);
        inputApellidoPaterno = view.findViewById(R.id.input_apellido_paterno);
        inputApellidoMaterno = view.findViewById(R.id.input_apellido_materno);
        inputCurp = view.findViewById(R.id.input_curp);
        inputClaveElector = view.findViewById(R.id.input_clave_elector);
        inputFechaNacimiento = view.findViewById(R.id.input_fecha_nacimiento);
        inputGenero = view.findViewById(R.id.input_genero);
        inputDomicilio = view.findViewById(R.id.input_domicilio);
        inputCiudad = view.findViewById(R.id.input_ciudad);
        inputEstado = view.findViewById(R.id.input_estado);
        inputCp = view.findViewById(R.id.input_cp);

        labelCurpValida = view.findViewById(R.id.label_curp_valida);
        labelIneValida = view.findViewById(R.id.label_ine_valida);

        btnEscanear = view.findViewById(R.id.btn_escanear_ine);
        btnValidar = view.findViewById(R.id.btn_validar_datos);
        btnSolicitar = view.findViewById(R.id.btn_crear_solicitud);

        imagenPreviewINE = view.findViewById(R.id.imagen_ine);
        previewView = view.findViewById(R.id.camera_preview);
        btnCapture = view.findViewById(R.id.btn_capture);
        btnFlashToggle = view.findViewById(R.id.btn_flash_toggle);
        cameraContainer = view.findViewById(R.id.camera_container);
        ineFrame = view.findViewById(R.id.ine_frame);

        // Inicializar ejecutor para la cámara
        cameraExecutor = ContextCompat.getMainExecutor(requireContext());

        // Configurar listeners
        btnEscanear.setOnClickListener(v -> toggleCameraView());
        btnCapture.setOnClickListener(v -> takePhoto());
        btnFlashToggle.setOnClickListener(v -> toggleFlash());
        btnValidar.setOnClickListener(v -> validarYGuardarCliente());
        btnSolicitar.setOnClickListener(v -> limpiarFormulario());

        // Ajustar tamaño del marco
        ajustarTamanioMarco();

        // Manejar argumentos (modo solo lectura o edición)
        Bundle args = getArguments();
        if (args != null && args.containsKey("nombre")) {
            modoSoloLectura = true;
            cargarDatosModoLectura(args);
        } else {
            configurarModoEdicion();
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Registrar launcher moderno para cámara
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String uriString = result.getData().getStringExtra("photoUri");
                        if (uriString != null) {
                            photoUri = Uri.parse(uriString);
                            imagenPreviewINE.setImageURI(photoUri);
                        }
                    }
                }
        );

        // Lanzador para pedir permiso cámara
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        toggleCameraView();
                    } else {
                        Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void cargarDatosModoLectura(Bundle args) {
        inputNombre.setText(args.getString("nombre", ""));
        inputApellidoPaterno.setText(args.getString("apellido_paterno", ""));
        inputApellidoMaterno.setText(args.getString("apellido_materno", ""));
        inputCurp.setText(args.getString("curp", ""));
        inputClaveElector.setText(args.getString("clave_elector", ""));
        inputFechaNacimiento.setText(args.getString("fecha_nacimiento", ""));
        inputGenero.setText(args.getString("genero", ""));
        inputDomicilio.setText(args.getString("domicilio", ""));
        inputCiudad.setText(args.getString("ciudad", ""));
        inputEstado.setText(args.getString("estado", ""));
        inputCp.setText(args.getString("codigo_postal", ""));

        ponerCamposSoloLectura(
                inputNombre, inputApellidoPaterno, inputApellidoMaterno,
                inputCurp, inputClaveElector, inputFechaNacimiento,
                inputGenero, inputDomicilio, inputCiudad,
                inputEstado, inputCp
        );

        btnEscanear.setVisibility(View.GONE);
        btnValidar.setVisibility(View.GONE);
        btnSolicitar.setVisibility(View.VISIBLE);
        btnSolicitar.setEnabled(true);

        btnSolicitar.setOnClickListener(v -> {
            int idCliente = args.getInt("id_cliente", -1);
            if (idCliente == -1) {
                Toast.makeText(getContext(), "No se encontró el cliente", Toast.LENGTH_SHORT).show();
                return;
            }
            SolicitudFinalFragment solicitudFinalFragment = new SolicitudFinalFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("id_cliente", idCliente);
            solicitudFinalFragment.setArguments(bundle);

            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.container_fragment, solicitudFinalFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        labelCurpValida.setVisibility(View.GONE);
        labelIneValida.setVisibility(View.GONE);
    }

    private void ponerCamposSoloLectura(EditText... campos) {
        for (EditText campo : campos) {
            campo.setEnabled(false);
            campo.setBackgroundColor(requireContext().getColor(android.R.color.darker_gray));
            campo.setTextColor(requireContext().getColor(android.R.color.black));
        }
    }

    private void toggleFlash() {
        isFlashEnabled = !isFlashEnabled;
        btnFlashToggle.setImageResource(
                isFlashEnabled ? R.drawable.ic_flash_on : R.drawable.ic_flash_off);
        if (imageCapture != null) {
            imageCapture.setFlashMode(
                    isFlashEnabled ? ImageCapture.FLASH_MODE_ON : ImageCapture.FLASH_MODE_OFF);
        }
    }

    private void configurarModoEdicion() {
        datosValidados = false;
        btnSolicitar.setEnabled(false);

        // Configurar selector fecha
        inputFechaNacimiento.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(getContext(), (view, y, m, d) -> {
                String fechaFormateada = String.format("%04d-%02d-%02d", y, m + 1, d);
                inputFechaNacimiento.setText(fechaFormateada);
            }, year, month, day);

            picker.show();
        });

        btnEscanear.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                toggleCameraView();
            }
        });
    }

    private void ajustarTamanioMarco() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int frameWidth = (int) (screenWidth * 0.8);
        int frameHeight = (int) (frameWidth * 0.633);

        ViewGroup.LayoutParams params = ineFrame.getLayoutParams();
        params.width = frameWidth;
        params.height = frameHeight;
        ineFrame.setLayoutParams(params);
    }

    private void toggleCameraView() {
        if (isCameraActive) {
            cameraContainer.setVisibility(View.GONE);
            btnCapture.setVisibility(View.GONE);
            btnEscanear.setText("Escanear INE");
            isCameraActive = false;
        } else {
            cameraContainer.setVisibility(View.VISIBLE);
            btnCapture.setVisibility(View.VISIBLE);
            btnEscanear.setText("Cancelar");
            isCameraActive = true;
            startCamera();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .setJpegQuality(90)
                        .setFlashMode(isFlashEnabled ? ImageCapture.FLASH_MODE_ON : ImageCapture.FLASH_MODE_OFF)
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(),
                        cameraSelector,
                        preview,
                        imageCapture
                );
            } catch (Exception e) {
                Log.e("CameraFragment", "Error al iniciar cámara", e);
                Toast.makeText(requireContext(), "Error al iniciar cámara", Toast.LENGTH_SHORT).show();
            }
        }, cameraExecutor);
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        photoFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "INE_" + System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        ineFrame.setBackgroundColor(Color.WHITE);
        ineFrame.postDelayed(() -> ineFrame.setBackgroundColor(Color.TRANSPARENT), 100);

        imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults output) {
                        photoUri = Uri.fromFile(photoFile);

                        requireActivity().runOnUiThread(() -> {
                            imagenPreviewINE.setImageURI(photoUri);
                            cameraContainer.setVisibility(View.GONE);
                            btnCapture.setVisibility(View.GONE);
                            btnEscanear.setText("Escanear INE");
                            isCameraActive = false;

                            Toast.makeText(requireContext(), "Foto guardada", Toast.LENGTH_SHORT).show();

                            enviarFotoAlBackend(photoFile);
                        });
                    }

                    @Override
                    public void onError(ImageCaptureException exception) {
                        Log.e("CameraFragment", "Error de captura: " + exception.getMessage());
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Error al capturar foto", Toast.LENGTH_SHORT).show());
                    }
                }
        );
    }

    private void enviarFotoAlBackend(File photoFile) {
        RequestBody requestFile = RequestBody.create(photoFile, MediaType.parse("image/jpeg"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("archivoINE", photoFile.getName(), requestFile);

        // Usar Retrofit configurado con timeout y logging desde ApiConfig
        Retrofit retrofit = ApiConfig.getRetrofit();
        UsuarioService service = retrofit.create(UsuarioService.class);

        service.enviarIne(body).enqueue(new Callback<OcrResponse>() {
            @Override
            public void onResponse(Call<OcrResponse> call, Response<OcrResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OcrResponse datos = response.body();

                    inputNombre.setText(datos.nombre);
                    inputApellidoPaterno.setText(datos.apellido_paterno);
                    inputApellidoMaterno.setText(datos.apellido_materno);
                    inputCurp.setText(datos.curp);
                    inputClaveElector.setText(datos.clave_elector);
                    inputFechaNacimiento.setText(datos.fecha_nacimiento);
                    inputGenero.setText(datos.sexo);
                    inputDomicilio.setText(datos.domicilio);
                    inputCiudad.setText(datos.municipio);
                    inputEstado.setText(datos.estado);
                    inputCp.setText(datos.codigo_postal);

                    labelIneValida.setText("INE escaneada correctamente");
                    labelIneValida.setTextColor(requireContext().getColor(android.R.color.holo_green_dark));
                } else {
                    Toast.makeText(getContext(), "Error al procesar INE", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OcrResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Falla al conectar OCR: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }




    private int obtenerIdUsuario() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CrediGoPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("id_usuario", -1);
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

    private RequestBody createPartFromString(String value) {
        return RequestBody.create(value != null ? value : "", MediaType.parse("text/plain"));
    }

    private void validarYGuardarCliente() {
        if (!datosValidados) {
            Toast.makeText(getContext(), "Validando y creando cliente...", Toast.LENGTH_SHORT).show();
        }

        // Aquí llamamos el mismo método que validarYGuardarCliente pero adaptado para redirigir y mostrar Toast con id
        String nombre = inputNombre.getText().toString().trim();
        String apellidoP = inputApellidoPaterno.getText().toString().trim();
        String apellidoM = inputApellidoMaterno.getText().toString().trim();
        String curp = inputCurp.getText().toString().trim();
        String claveElector = inputClaveElector.getText().toString().trim();
        String fechaNacimiento = inputFechaNacimiento.getText().toString().trim();
        String genero = inputGenero.getText().toString().trim();
        String domicilio = inputDomicilio.getText().toString().trim();
        String ciudad = inputCiudad.getText().toString().trim();
        String estado = inputEstado.getText().toString().trim();
        String codigoPostal = inputCp.getText().toString().trim();

        if (nombre.isEmpty() || curp.isEmpty() || photoFile == null) {
            Toast.makeText(getContext(), "Datos incompletos o INE no capturado", Toast.LENGTH_SHORT).show();
            return;
        }

        int idUsuario = obtenerIdUsuario();
        if (idUsuario == -1) {
            Toast.makeText(getContext(), "Error: no se encontró el usuario logeado", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody nombrePart = createPartFromString(nombre);
        RequestBody apellidoPPart = createPartFromString(apellidoP);
        RequestBody apellidoMPart = createPartFromString(apellidoM);
        RequestBody curpPart = createPartFromString(curp);
        RequestBody claveElectorPart = createPartFromString(claveElector);
        RequestBody fechaNacimientoPart = createPartFromString(fechaNacimiento);
        RequestBody generoPart = createPartFromString(genero);
        RequestBody domicilioPart = createPartFromString(domicilio);
        RequestBody ciudadPart = createPartFromString(ciudad);
        RequestBody estadoPart = createPartFromString(estado);
        RequestBody codigoPostalPart = createPartFromString(codigoPostal);
        RequestBody idUsuarioPart = createPartFromString(String.valueOf(idUsuario));

        RequestBody requestFile = RequestBody.create(photoFile, MediaType.parse("image/jpeg"));
        MultipartBody.Part archivoINEPart = MultipartBody.Part.createFormData("ArchivoINE", photoFile.getName(), requestFile);

        Retrofit retrofit = ApiConfig.getRetrofit();
        UsuarioService service = retrofit.create(UsuarioService.class);


        Call<Cliente> call = service.validarYGuardarCliente(
                nombrePart,
                apellidoPPart,
                apellidoMPart,
                curpPart,
                claveElectorPart,
                fechaNacimientoPart,
                generoPart,
                domicilioPart,
                ciudadPart,
                estadoPart,
                codigoPostalPart,
                idUsuarioPart,
                archivoINEPart
        );

        call.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Cliente clienteCreado = response.body();
                    int idCliente = clienteCreado.idCliente;

                    Toast.makeText(getContext(), "Cliente creado con id: " + idCliente, Toast.LENGTH_SHORT).show();

                    // Redirigir al fragmento de solicitud final
                    SolicitudFinalFragment solicitudFinalFragment = new SolicitudFinalFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("id_cliente", idCliente);
                    solicitudFinalFragment.setArguments(bundle);

                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_fragment, solicitudFinalFragment)
                            .addToBackStack(null)
                            .commit();

                } else {
                    Toast.makeText(getContext(), "Error al crear cliente", Toast.LENGTH_SHORT).show();
                    Log.e("CrearSolicitud", "Error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                Toast.makeText(getContext(), "Falla en conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void limpiarFormulario() {
        inputNombre.setText("");
        inputApellidoPaterno.setText("");
        inputApellidoMaterno.setText("");
        inputCurp.setText("");
        inputClaveElector.setText("");
        inputFechaNacimiento.setText("");
        inputGenero.setText("");
        inputDomicilio.setText("");
        inputCiudad.setText("");
        inputEstado.setText("");
        inputCp.setText("");


        labelCurpValida.setText("");
        labelIneValida.setText("");
        datosValidados = false;
        btnSolicitar.setEnabled(false);


        photoFile = null;
        photoUri = null;
    }
}
