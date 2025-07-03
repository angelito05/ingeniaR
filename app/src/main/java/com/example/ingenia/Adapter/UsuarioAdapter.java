package com.example.ingenia.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ingenia.Model.User;
import com.example.ingenia.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    @SuppressLint("SetTextI18n")
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

            holder.iconoEstado.setImageResource(R.drawable.ic_profile);
            int color = user.isActivo() ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336");
            holder.iconoEstado.setColorFilter(color);
            // Mostrar fecha (puedes formatearla si lo deseas)
            holder.fechaCreacion.setText("Fecha de creación: " + user.getFecha_creacion());
            try {
                // Reemplazar punto por coma si es necesario
                String fechaOriginal = user.getFecha_creacion().replace("T", " "); // Por si viene con T
                SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                Date fecha = formatoEntrada.parse(fechaOriginal);
                holder.fechaCreacion.setText("Fecha de creación: " + formatoSalida.format(fecha));

            } catch (Exception e) {
                // Si falla por milisegundos o algún otro error, intenta sin milisegundos
                try {
                    SimpleDateFormat formatoEntradaSimple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                    Date fecha = formatoEntradaSimple.parse(user.getFecha_creacion());
                    holder.fechaCreacion.setText("Fecha de creación: " + formatoSalida.format(fecha));
                } catch (Exception ex) {
                    holder.fechaCreacion.setText("Fecha inválida");
                }
            }

        } catch (Exception e) {
            Log.e("UsuarioAdapter", "Error en onBindViewHolder", e);
        }
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

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvUsername);
            detalles = itemView.findViewById(R.id.tvDetalles);
            fechaCreacion = itemView.findViewById(R.id.tvFechaCreacion);
            iconoEstado = itemView.findViewById(R.id.iconoEstado);
            layoutAcciones = itemView.findViewById(R.id.accionesUsuario);

        }
    }
}
