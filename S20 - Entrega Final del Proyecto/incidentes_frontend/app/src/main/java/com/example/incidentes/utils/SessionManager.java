package com.example.incidentes.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.incidentes.Usuario;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_ID = "id";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROL = "rol";
    private static final String KEY_AVATAR = "avatar"; // Nueva clave para el avatar

    // Guardar la información del usuario
    public static void saveUser(Context context, Usuario user) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_NOMBRE, user.getNombre());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_ROL, user.getRol());
        editor.putString(KEY_AVATAR, user.getAvatar()); // Guardar el avatar en base64 si se usa
        editor.apply();
    }

    // Obtener el ID del usuario
    public static int getUserId(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(KEY_ID, -1);
    }

    // Obtener el nombre del usuario
    public static String getUserName(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_NOMBRE, "");
    }

    // ✅ Agregado: Obtener el email del usuario
    public static String getUserEmail(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_EMAIL, "");
    }

    // Obtener el rol del usuario
    public static String getUserRole(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_ROL, "");
    }

    // Obtener el avatar del usuario (base64)
    public static String getUserAvatar(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_AVATAR, null);
    }

    // ✅ Agregado: Guardar el avatar del usuario (base64)
    public static void saveUserAvatar(Context context, String avatarBase64) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_AVATAR, avatarBase64);
        editor.apply();
    }

    // Comprobar si el usuario tiene una sesión activa
    public static boolean isUserLoggedIn(Context context) {
        return getUserId(context) != -1;
    }

    // Cerrar sesión y eliminar los datos del usuario
    public static void logoutUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();  // Elimina todos los datos
        editor.apply();
    }
}




