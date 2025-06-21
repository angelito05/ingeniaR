package com.example.ingenia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public SolicitudAdapter(Context context, List<Solicitud> lista) {
        this.context = context;
        this.lista = lista;
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

        // Cambia ícono según estado
        switch (solicitud.estado) {
            case APROBADA:
                holder.icono.setImageResource(R.drawable.ic_check);    // verde
                holder.icono.setColorFilter(0xFF4CAF50);
                break;
            case RECHAZADA:
                holder.icono.setImageResource(R.drawable.ic_delete);   // rojo
                holder.icono.setColorFilter(0xFFF44336);
                break;
            case PENDIENTE:
                holder.icono.setImageResource(R.drawable.ic_pending);  // amarillo
                holder.icono.setColorFilter(0xFFFFC107);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, detalles;
        ImageView icono;

        ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            detalles = itemView.findViewById(R.id.detalles);
            icono = itemView.findViewById(R.id.iconoEstado);
        }
    }
}
