package com.credigo.ingenia.View;
import android.widget.EditText;

import com.credigo.ingenia.Model.Usuario;
import com.credigo.ingenia.Model.Validator;
public class MainController {
    public static boolean validarFormulario(EditText nombreField, EditText apPatField, EditText apMatField, EditText correoField) {
        String nombre = nombreField.getText().toString().trim();
        String apPaterno = apPatField.getText().toString().trim();
        String apMaterno = apMatField.getText().toString().trim();
        String correo = correoField.getText().toString().trim();

        boolean correoValido = Validator.esCorreoValido(correo);
        boolean camposValidos = Validator.camposLlenos(nombre, apPaterno, apMaterno);

        if (!correoValido) {
            correoField.setError("Correo inválido");
        }

        return correoValido && camposValidos;
    }

    public static Usuario construirUsuario(EditText nombre, EditText apPat, EditText apMat,
                                           EditText username, EditText correo, EditText contra) {
        return new Usuario(
                nombre.getText().toString().trim(),
                apPat.getText().toString().trim(),
                apMat.getText().toString().trim(),
                username.getText().toString().trim(),
                correo.getText().toString().trim(),
                contra.getText().toString().trim()
        );
    }
}
