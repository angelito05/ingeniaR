package com.example.ingenia.Adapter;

import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ingenia.Model.SolicitudCredito;
import com.example.ingenia.R;

import java.util.List;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.ViewHolder> {

    private final List<SolicitudCredito> lista;
    private final boolean esAdmin;

    public SolicitudAdapter(List<SolicitudCredito> lista, boolean esAdmin) {
        this.lista = lista;
        this.esAdmin = esAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_solicitud, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SolicitudCredito sc = lista.get(position);

        holder.nombre.setText("Cliente #" + sc.id_cliente);
        holder.detalles.setText(
                "Plazo: " + sc.plazo_meses + " meses\n" +
                        "Monto: $" + sc.monto_solicitado + "\n" +
                        "Motivo: " + sc.motivo
        );

        // Solo mostrar botones y trabajador si es admin
        if (esAdmin) {
            holder.trabajador.setVisibility(View.VISIBLE);
            holder.trabajador.setText("Registrado por: Usuario " + sc.id_usuario); // Puedes mejorar si tienes el nombre
            holder.botonesEstado.setVisibility(View.VISIBLE);

            holder.btnAprobar.setOnClickListener(v -> {
                // Aquí puedes implementar lógica para actualizar estado al servidor
                sc.id_estatus = 2;
                notifyItemChanged(position);
            });

            holder.btnRechazar.setOnClickListener(v -> {
                sc.id_estatus = 3;
                notifyItemChanged(position);
            });
        } else {
            holder.trabajador.setVisibility(View.GONE);
            holder.botonesEstado.setVisibility(View.GONE);
        }

        // Icono por estado
        switch (sc.id_estatus) {
            case 1:
                holder.icono.setImageResource(R.drawable.ic_pending);
                holder.icono.setColorFilter(0xFFFFC107); // amarillo
                break;
            case 2:
                holder.icono.setImageResource(R.drawable.ic_check);
                holder.icono.setColorFilter(0xFF4CAF50); // verde
                break;
            case 3:
                holder.icono.setImageResource(R.drawable.ic_delete);
                holder.icono.setColorFilter(0xFFF44336); // rojo
                break;
            default:
                holder.icono.setImageResource(R.drawable.ic_pending);
                holder.icono.setColorFilter(0xFFAAAAAA); // gris por defecto
                break;
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, detalles, trabajador;
        ImageView icono;
        Button btnAprobar, btnRechazar;
        View botonesEstado;

        ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            detalles = itemView.findViewById(R.id.detalles);
            trabajador = itemView.findViewById(R.id.trabajador);
            btnAprobar = itemView.findViewById(R.id.btnAprobar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
            botonesEstado = itemView.findViewById(R.id.botonesEstado);
            icono = itemView.findViewById(R.id.iconoEstado);
        }
    }
}
