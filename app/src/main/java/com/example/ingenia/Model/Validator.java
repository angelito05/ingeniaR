package com.example.ingenia.Model;

public class Validator {
    public static boolean esCorreoValido(String email) {
        if (email == null) return false;
        email = email.trim();
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";
        return java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(email)
                .matches();
    }

    public static boolean camposLlenos(String nombre, String apPaterno, String apMaterno) {
        return !nombre.isEmpty() && !apPaterno.isEmpty() && !apMaterno.isEmpty();
    }

    public static boolean esContrasenaSegura(String contrasena) {
        return contrasena.length() >= 8 &&
                contrasena.matches(".*[A-Z].*") &&
                contrasena.matches(".*\\d.*");
    }

    public static String[] sugerenciasContrasena(String contrasena) {
        java.util.List<String> sugerencias = new java.util.ArrayList<>();
        if (contrasena.length() < 8) sugerencias.add("Mínimo 8 caracteres");
        if (!contrasena.matches(".*[A-Z].*")) sugerencias.add("Debe incluir al menos una mayúscula");
        if (!contrasena.matches(".*\\d.*")) sugerencias.add("Debe incluir al menos un número");
        return sugerencias.toArray(new String[0]);
    }
}
