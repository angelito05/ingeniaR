package com.example.ingenia.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ingenia.Model.BitacoraDTO;
import com.example.ingenia.R;

import java.util.List;

public class BitacoraAdapter extends RecyclerView.Adapter<BitacoraAdapter.ViewHolder> {

    private List<BitacoraDTO> lista;

    public BitacoraAdapter(List<BitacoraDTO> lista) {
        this.lista = lista;
    }

    public void actualizarLista(List<BitacoraDTO> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BitacoraAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bitacora, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull BitacoraAdapter.ViewHolder holder, int position) {
        BitacoraDTO item = lista.get(position);

        holder.txtUsuario.setText(item.getNombreUsuario());
        holder.txtAccion.setText(item.getAccion());
        holder.txtDescripcion.setText(item.getDescripcion());
        holder.txtEntidad.setText(item.getEntidad_afectada());

        String cliente = item.getNombreClienteAfectado();
        holder.txtCliente.setText((cliente != null && !cliente.trim().isEmpty()) ? cliente : "-");
        holder.txtFecha.setText(item.getFecha());

        // Declaramos la condición aquí:
        boolean esValidarCurp = "VALIDAR_CURP".equalsIgnoreCase(item.getAccion());
        boolean clienteVacio = cliente == null || cliente.trim().isEmpty();

        // Pintamos en rojo si es VALIDAR_CURP sin cliente
        if (esValidarCurp && clienteVacio) {
            holder.txtAccion.setTextColor(holder.itemView.getContext().getColor(R.color.danger));
        } else {
            holder.txtAccion.setTextColor(holder.itemView.getContext().getColor(R.color.green));
        }
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsuario, txtAccion, txtDescripcion, txtEntidad, txtCliente, txtFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsuario = itemView.findViewById(R.id.txtUsuario);
            txtAccion = itemView.findViewById(R.id.txtAccion);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtEntidad = itemView.findViewById(R.id.txtEntidad);
            txtCliente = itemView.findViewById(R.id.txtCliente);
            txtFecha = itemView.findViewById(R.id.txtFecha);
        }
    }
}
