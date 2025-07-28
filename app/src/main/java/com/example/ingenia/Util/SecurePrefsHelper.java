package com.example.ingenia.Util;


import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;


import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecurePrefsHelper {
    private static final String PREFS_NAME = "CrediGoSecurePrefs";
    private final SharedPreferences encryptedPrefs;
    public SecurePrefsHelper(Context context) throws Exception {
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }
    public void guardarIdUsuario(int idUsuario){
        encryptedPrefs.edit().putInt("id_usuario", idUsuario ).apply();
    }
    public int obtenerIdUsuario(){
        return encryptedPrefs.getInt("id_usuario", -1);
    }
    public void limpiarTodo(){
        encryptedPrefs.edit().clear().apply();
    }
}
