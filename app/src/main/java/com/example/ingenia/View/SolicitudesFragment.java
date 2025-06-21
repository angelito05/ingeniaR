package com.example.ingenia.View;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ingenia.Model.Solicitud;
import com.example.ingenia.Adapter.SolicitudAdapter;
import com.example.ingenia.R;

import java.util.ArrayList;
import java.util.List;

public class SolicitudesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView total, aprobadas, rechazadas;

    public SolicitudesFragment() {
        // Constructor vacío obligatorio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solicitudes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencias a vistas
        recyclerView = view.findViewById(R.id.recyclerSolicitudes);
        total = view.findViewById(R.id.contadorTotal);
        aprobadas = view.findViewById(R.id.contadorAprobadas);
        rechazadas = view.findViewById(R.id.contadorRechazadas);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lista de datos (temporal, de prueba)
        List<Solicitud> solicitudes = new ArrayList<>();
        solicitudes.add(new Solicitud("RONALD LEYVA",
                "CURP VERIFICADA\nINE VERIFICADA\nPLAZO 6-12 MESES\nMONTO: $6,000",
                Solicitud.Estado.APROBADA));

        solicitudes.add(new Solicitud("ANGELITO ISSAC",
                "CURP VERIFICADA\nINE VERIFICADA\nPLAZO 6-12 MESES\nMONTO: $6,000",
                Solicitud.Estado.PENDIENTE));

        solicitudes.add(new Solicitud("MARCO ASTUDILLO",
                "CURP VERIFICADA\nINE VERIFICADA\nPLAZO 6-12 MESES\nMONTO: $6,000",
                Solicitud.Estado.APROBADA));

        recyclerView.setAdapter(new SolicitudAdapter(requireContext(), solicitudes));

        // Actualizar contadores
        int aprobadasCount = 0, rechazadasCount = 0;
        for (Solicitud s : solicitudes) {
            if (s.estado == Solicitud.Estado.APROBADA) aprobadasCount++;
            else if (s.estado == Solicitud.Estado.RECHAZADA) rechazadasCount++;
        }

        total.setText("Total de solicitudes: " + solicitudes.size());
        aprobadas.setText("Aprobadas: " + aprobadasCount);
        rechazadas.setText("Rechazadas: " + rechazadasCount);
    }
}
