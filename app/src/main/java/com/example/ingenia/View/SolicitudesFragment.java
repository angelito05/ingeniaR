package com.example.ingenia.View;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ingenia.R;

public class SolicitudesFragment extends Fragment {

    public SolicitudesFragment() {
        // Constructor vacío obligatorio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout de este fragmento
        return inflater.inflate(R.layout.fragment_solicitudes, container, false);
    }
}
