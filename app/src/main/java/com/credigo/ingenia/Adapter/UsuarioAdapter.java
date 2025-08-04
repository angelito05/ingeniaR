package com.credigo.ingenia.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.credigo.ingenia.Model.User;
import com.credigo.ingenia.Model.UsuarioActualizarDTO;
import com.credigo.ingenia.R;
import com.credigo.ingenia.api.ApiConfig;
import com.credigo.ingenia.api.UsuarioService;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private final Context context;
    private final List<User> usuarios;

    public UsuarioAdapter(Context context, List<User> usuarios) {
        this.context = context;
        this.usuarios = usuarios;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }
    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        try {
            User user = usuarios.get(position);

            holder.username.setText(user.getUsername());

            String rolTexto = user.getId_rol() == 1 ? "Administrador" : "Empleado";
            String estadoTexto = user.isActivo() ? "Activo" : "Inactivo";

            holder.detalles.setText("Correo: " + user.getCorreo()
                    + "\nRol: " + rolTexto
                    + "\nEstado: " + estadoTexto);

            // Configurar texto y acción del botón para activar/desactivar usuario
            holder.btnCambiarEstado.setText(user.isActivo() ? "Desactivar" : "Activar");

            // Guardar el ID de usuario en el botón
            holder.btnCambiarEstado.setTag(user.getId_usuario());

            // Configurar listener del botón
            holder.btnCambiarEstado.setOnClickListener(v -> {
                int userId = (int) v.getTag();
                cambiarEstadoUsuario(userId, !user.isActivo());
            });

            // Manejo de fechas (código existente)
            try {
                String fechaOriginal = user.getFecha_creacion().replace("T", " ");
                SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                Date fecha = formatoEntrada.parse(fechaOriginal);
                holder.fechaCreacion.setText("Fecha de creación: " + formatoSalida.format(fecha));
            } catch (Exception e) {
                try {
                    SimpleDateFormat formatoEntradaSimple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                    Date fecha = formatoEntradaSimple.parse(user.getFecha_creacion());
                    holder.fechaCreacion.setText("Fecha de creación: " + formatoSalida.format(fecha));
                } catch (Exception ex) {
                    holder.fechaCreacion.setText("Fecha inválida");
                }
            }
            holder.itemView.setOnClickListener(v -> {
                mostrarDialogoEditarUsuario(holder.getAdapterPosition());
            });

        } catch (Exception e) {
            Log.e("UsuarioAdapter", "Error en onBindViewHolder", e);
        }



    }
    private void cambiarEstadoUsuario(int userId, boolean nuevoEstado) {
        // Crear DTO solo con el campo activo
        UsuarioActualizarDTO dto = new UsuarioActualizarDTO(null, null, nuevoEstado);

        UsuarioService service = ApiConfig.getRetrofit().create(UsuarioService.class);
        Call<User> call = service.actualizarUsuario(userId, dto);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Buscar la posición actual del usuario actualizado
                    int updatedPosition = -1;
                    for (int i = 0; i < usuarios.size(); i++) {
                        if (usuarios.get(i).getId_usuario() == userId) {
                            updatedPosition = i;
                            break;
                        }
                    }

                    if (updatedPosition != -1) {
                        // Actualizar el usuario en la lista local
                        usuarios.set(updatedPosition, response.body());
                        notifyItemChanged(updatedPosition);
                        Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Error al cambiar estado", Toast.LENGTH_SHORT).show();
                    if (response.errorBody() != null) {
                        try {
                            Log.e("API_ERROR", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(context, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("NETWORK_ERROR", "Error en cambiarEstadoUsuario", t);
            }
        });
    }

    private void mostrarDialogoEditarUsuario(int position) {
        User user = usuarios.get(position);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_editar_usuario, null);
        EditText etUsername = dialogView.findViewById(R.id.etEditarUsername);
        EditText etPassword = dialogView.findViewById(R.id.etEditarPassword);
        CheckBox checkActivo = dialogView.findViewById(R.id.checkActivo);
        Button btnEliminar = dialogView.findViewById(R.id.btnEliminarUsuario);

        etUsername.setText(user.getUsername());
        checkActivo.setChecked(user.isActivo());

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Editar Usuario")
                .setView(dialogView)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.setOnShowListener(dlg -> {
            Button btnGuardar = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnGuardar.setOnClickListener(v -> {
                String nuevoUsername = etUsername.getText().toString().trim();
                String nuevaPass = etPassword.getText().toString().trim();
                boolean nuevoEstado = checkActivo.isChecked();

                UsuarioActualizarDTO dto = new UsuarioActualizarDTO(
                        nuevoUsername.isEmpty() ? null : nuevoUsername,
                        nuevaPass.isEmpty() ? null : nuevaPass,
                        nuevoEstado
                );

                UsuarioService api = ApiConfig.getRetrofit().create(UsuarioService.class);
                api.actualizarUsuario(user.getId_usuario(), dto).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            usuarios.set(position, response.body());
                            notifyItemChanged(position);
                            Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(context, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });

            btnEliminar.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("¿Eliminar usuario?")
                        .setMessage("Esta acción no se puede deshacer. ¿Deseas continuar?")
                        .setPositiveButton("Sí, eliminar", (d, w) -> {
                            UsuarioService api = ApiConfig.getRetrofit().create(UsuarioService.class);
                            api.eliminarUsuario(user.getId_usuario()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        usuarios.remove(position);
                                        notifyItemRemoved(position);
                                        Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(context, "Error al eliminar usuario", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(context, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });
        });

        dialog.show();
    }




    @Override
    public int getItemCount() {
        return usuarios.size();
    }


    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {

        TextView username, detalles;
        TextView fechaCreacion;

        ImageView iconoEstado;
        LinearLayout layoutAcciones;
        Button btnCambiarEstado;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvUsername);
            detalles = itemView.findViewById(R.id.tvDetalles);
            fechaCreacion = itemView.findViewById(R.id.tvFechaCreacion);
            iconoEstado = itemView.findViewById(R.id.iconoEstado);
            btnCambiarEstado = itemView.findViewById(R.id.btnCambiarEstado);
            layoutAcciones = itemView.findViewById(R.id.accionesUsuario);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void actualizarLista(List<User> nuevaLista) {
        usuarios.clear();
        usuarios.addAll(nuevaLista);
        notifyDataSetChanged();
    }

}
