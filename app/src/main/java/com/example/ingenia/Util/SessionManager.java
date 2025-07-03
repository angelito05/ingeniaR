package com.example.ingenia.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ingenia.Model.User;

public class SessionManager {
    private static final String PREF_NAME = "CrediGoPrefs";
    private static final String KEY_ID_USUARIO = "id_usuario";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_CORREO = "correo";
    private static final String KEY_ID_ROL = "id_rol";
    private static final String KEY_ACTIVO = "activo";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(User user) {
        editor.putInt(KEY_ID_USUARIO, user.getId_usuario());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_CORREO, user.getCorreo());
        editor.putInt(KEY_ID_ROL, user.getId_rol());
        editor.putBoolean(KEY_ACTIVO,user.isActivo());
        editor.apply();
    }

    public int getIdUsuario() {
        return prefs.getInt(KEY_ID_USUARIO, -1);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getCorreo() { return prefs.getString(KEY_CORREO, ""); }
    public int getIdRol() {
        return prefs.getInt(KEY_ID_ROL, -1);
    }
    public boolean isActivo() { return prefs.getBoolean(KEY_ACTIVO, true); }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
