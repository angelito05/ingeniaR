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

import java.util.List;

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
