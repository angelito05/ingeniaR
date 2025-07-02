package com.example.ingenia.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "CrediGoPrefs";
    private static final String KEY_ID_USUARIO = "id_usuario";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ID_ROL = "id_rol";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(int idUsuario, String username, int idRol) {
        editor.putInt(KEY_ID_USUARIO, idUsuario);
        editor.putString(KEY_USERNAME, username);
        editor.putInt(KEY_ID_ROL, idRol);
        editor.apply();
    }

    public int getIdUsuario() {
        return prefs.getInt(KEY_ID_USUARIO, -1);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public int getIdRol() {
        return prefs.getInt(KEY_ID_ROL, -1);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
