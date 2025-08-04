package com.credigo.ingenia.View;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.credigo.ingenia.R;

public class MensajeAnimadoDialogFragment extends DialogFragment {

    private static final String ARG_MENSAJE = "mensaje";
    private static final String ARG_EXITO = "esExito";

    public static MensajeAnimadoDialogFragment newInstance(String mensaje, boolean esExito) {
        MensajeAnimadoDialogFragment fragment = new MensajeAnimadoDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MENSAJE, mensaje);
        args.putBoolean(ARG_EXITO, esExito);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mensaje_animado_dialog, container, false);

        TextView textoMensaje = view.findViewById(R.id.textoMensaje);
        LottieAnimationView lottieMensaje = view.findViewById(R.id.lottieMensaje);

        Bundle args = getArguments();
        if (args != null) {
            String mensaje = args.getString(ARG_MENSAJE);
            boolean esExito = args.getBoolean(ARG_EXITO);

            textoMensaje.setText(mensaje);
            lottieMensaje.setAnimation(esExito ? R.raw.tick_mark : R.raw.cross);
            lottieMensaje.playAnimation();
        }

        // Cierra automáticamente después de 3 segundos
        new Handler(Looper.getMainLooper()).postDelayed(this::dismiss, 6000);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onCancel(@NonNull android.content.DialogInterface dialog) {
        // No se cierra con botón atrás (opcional)
    }
    public static void mostrarMensaje(androidx.fragment.app.FragmentManager fragmentManager, String mensaje, boolean esExito) {
        MensajeAnimadoDialogFragment fragment = MensajeAnimadoDialogFragment.newInstance(mensaje, esExito);
        fragment.show(fragmentManager, "mensaje_animado");
    }
}
