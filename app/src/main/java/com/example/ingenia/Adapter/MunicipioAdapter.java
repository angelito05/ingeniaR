package com.example.ingenia.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ingenia.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MunicipioAdapter extends RecyclerView.Adapter<MunicipioAdapter.MunicipioViewHolder> {

    private final List<Map.Entry<String, Integer>> municipios;

    public MunicipioAdapter(Map<String, Integer> datos) {
        this.municipios = new ArrayList<>(datos.entrySet());
    }

    @NonNull
    @Override
    public MunicipioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_municipio_kpi, parent, false);
        return new MunicipioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MunicipioViewHolder holder, int position) {
        Map.Entry<String, Integer> entry = municipios.get(position);
        holder.txtMunicipio.setText(entry.getKey());
        holder.txtCantidad.setText(entry.getValue() + " solicitudes");
    }

    @Override
    public int getItemCount() {
        return municipios.size();
    }

    static class MunicipioViewHolder extends RecyclerView.ViewHolder {
        TextView txtMunicipio, txtCantidad;

        public MunicipioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMunicipio = itemView.findViewById(R.id.txtMunicipio);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
        }
    }
}
