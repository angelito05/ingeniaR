package com.example.ingenia.Adapter;

import android.view.*;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ingenia.Model.Cliente;
import com.example.ingenia.R;

import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<Cliente> listaClientes;

    public ClienteAdapter(List<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
        return new ClienteViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente cliente = listaClientes.get(position);

        // Asignar datos
        String nombreCompleto = cliente.nombre + " " + cliente.apellido_paterno + " " + cliente.apellido_materno;
        holder.txtNombre.setText(nombreCompleto);
        holder.txtCiudad.setText("Ciudad: " + cliente.ciudad);
        holder.txtCurp.setText("CURP: " + cliente.curp);
        holder.txtClaveElector.setText("Clave de elector: " + cliente.clave_elector);
        holder.txtFechaNacimiento.setText("Fecha de nacimiento: " + cliente.fecha_nacimiento);

        // Botón "Ver detalles"
        holder.btnVerDetalles.setOnClickListener(v -> {
            // Aquí puedes hacer lo que quieras: mostrar un diálogo, abrir otra pantalla, etc.
            // Ejemplo simple:
            // Toast.makeText(v.getContext(), "Cliente: " + nombreCompleto, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }

    public static class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCiudad, txtCurp, txtClaveElector, txtFechaNacimiento;
        Button btnVerDetalles;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreCliente);
            txtCiudad = itemView.findViewById(R.id.txtCiudadCliente);
            txtCurp = itemView.findViewById(R.id.txtCurpCliente);
            txtClaveElector = itemView.findViewById(R.id.txtClaveElector);
            txtFechaNacimiento = itemView.findViewById(R.id.txtFechaNacimiento);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
        }
    }
}
