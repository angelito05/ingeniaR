package com.example.ingenia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ingenia.Model.Solicitud;
import com.example.ingenia.R;

import java.util.List;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.ViewHolder> {

    private final List<Solicitud> lista;
    private final Context context;
    private final boolean esAdmin;

    public SolicitudAdapter(Context context, List<Solicitud> lista, boolean esAdmin) {
        this.context = context;
        this.lista = lista;
        this.esAdmin = esAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_solicitud, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Solicitud solicitud = lista.get(position);

        holder.nombre.setText(solicitud.nombre);
        holder.detalles.setText(solicitud.detalles);

        // Mostrar trabajador solo si es admin
        if (esAdmin) {
            holder.trabajador.setVisibility(View.VISIBLE);
            holder.trabajador.setText("Registrado por: " + solicitud.trabajador);
        } else {
            holder.trabajador.setVisibility(View.GONE);
        }

        // Mostrar botones solo para admin
        holder.botonesEstado.setVisibility(esAdmin ? View.VISIBLE : View.GONE);

        // Asignar color/ícono por estado
        switch (solicitud.estado) {
            case APROBADA:
                holder.icono.setImageResource(R.drawable.ic_check);
                holder.icono.setColorFilter(0xFF4CAF50); // verde
                break;
            case RECHAZADA:
                holder.icono.setImageResource(R.drawable.ic_delete);
                holder.icono.setColorFilter(0xFFF44336); // rojo
                break;
            case PENDIENTE:
                holder.icono.setImageResource(R.drawable.ic_pending);
                holder.icono.setColorFilter(0xFFFFC107); // amarillo
                break;
        }

        // Acciones de botones
        holder.btnAprobar.setOnClickListener(v -> {
            solicitud.estado = Solicitud.Estado.APROBADA;
            notifyItemChanged(position);
        });

        holder.btnRechazar.setOnClickListener(v -> {
            solicitud.estado = Solicitud.Estado.RECHAZADA;
            notifyItemChanged(position);
        });
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
            icono = itemView.findViewById(R.id.iconoEstado);
            trabajador = itemView.findViewById(R.id.trabajador); // ¡DEBES tener este en tu layout!
            btnAprobar = itemView.findViewById(R.id.btnAprobar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
            botonesEstado = itemView.findViewById(R.id.botonesEstado);
        }
    }
}
