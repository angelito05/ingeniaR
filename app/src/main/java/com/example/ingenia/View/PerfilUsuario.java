package com.example.ingenia.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ingenia.R;
import com.example.ingenia.databinding.FragmentPerfilUsuarioBinding;

public class PerfilUsuario extends Fragment {

    private FragmentPerfilUsuarioBinding binding;

    public PerfilUsuario() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPerfilUsuarioBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

}