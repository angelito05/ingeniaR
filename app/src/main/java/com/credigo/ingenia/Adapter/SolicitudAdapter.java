package com.credigo.ingenia.Adapter;

import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.credigo.ingenia.Model.SolicitudCredito;
import com.credigo.ingenia.R;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.ViewHolder> {

    private final List<SolicitudCredito> lista;
    private final boolean esAdmin;
    private final BiConsumer<Integer, Integer> cambiarEstatusCallback;
    private final Consumer<Integer> eliminarCallback;
    private final OnItemClickListener itemClickListener; // <-- NUEVO

    public interface OnItemClickListener {
        void onItemClick(SolicitudCredito solicitud);
    }

    public SolicitudAdapter(List<SolicitudCredito> lista,
                            boolean esAdmin,
                            BiConsumer<Integer, Integer> cambiarEstatusCallback,
                            Consumer<Integer> eliminarCallback,
                            OnItemClickListener itemClickListener) {
        this.lista = lista;
        this.esAdmin = esAdmin;
        this.cambiarEstatusCallback = cambiarEstatusCallback;
        this.eliminarCallback = eliminarCallback;
        this.itemClickListener = itemClickListener;
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

        holder.nombre.setText(sc.nombreCliente);
        holder.detalles.setText(
                "Plazo: " + sc.plazo_meses + " meses\n" +
                        "Monto: $" + sc.monto_solicitado + "\n" +
                        "Motivo: " + sc.motivo
        );
        String interesTexto = String.format("Interés: %.2f%% - Mensualidad: $%.2f",
                sc.tasa_interes, sc.pago_mensual_estimado);
        holder.infoFinanciera.setText(interesTexto);




        // Mostrar controles si es admin
        if (esAdmin) {
            holder.trabajador.setVisibility(View.VISIBLE);
            holder.trabajador.setText("Registrado por: " + sc.nombreUsuario);
            holder.botonesEstado.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);

            holder.btnAprobar.setOnClickListener(v -> cambiarEstatusCallback.accept(sc.id_solicitud, 2));
            holder.btnRechazar.setOnClickListener(v -> cambiarEstatusCallback.accept(sc.id_solicitud, 3));
            holder.btnEliminar.setOnClickListener(v -> eliminarCallback.accept(sc.id_solicitud));
        } else {
            holder.trabajador.setVisibility(View.GONE);
            holder.botonesEstado.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);

            // Si no es admin, al hacer clic en la tarjeta se ejecuta el callback
            holder.itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(sc);
                }
            });
        }

        // Icono de estado
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
                holder.icono.setColorFilter(0xFFAAAAAA); // gris
                break;
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void actualizarLista(List<SolicitudCredito> nuevaLista) {
        lista.clear();
        lista.addAll(nuevaLista);
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, detalles, trabajador;
        TextView infoFinanciera;

        ImageView icono;
        Button btnAprobar, btnRechazar;
        ImageButton btnEliminar;
        View botonesEstado;

        ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            detalles = itemView.findViewById(R.id.detalles);
            trabajador = itemView.findViewById(R.id.trabajador);
            btnAprobar = itemView.findViewById(R.id.btnAprobar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            botonesEstado = itemView.findViewById(R.id.botonesEstado);
            icono = itemView.findViewById(R.id.iconoEstado);
            infoFinanciera = itemView.findViewById(R.id.infoFinanciera);

        }
    }
}
