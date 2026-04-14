package com.lesadrax.registrationclient.sessionManager;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "user_session"; // Nom du fichier SharedPreferences
    private static final String KEY_ACCESS_TOKEN = "accessToken"; // Clé pour stocker l'accessToken
    private static final String KEY_IS_FIRST_CONNECTION = "isFirstConnection"; // Clé pour gérer la première connexion

    private static final String ID = "ID";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Initialiser isFirstConnection à true si elle n'existe pas
        if (!sharedPreferences.contains(KEY_IS_FIRST_CONNECTION)) {
            editor.putBoolean(KEY_IS_FIRST_CONNECTION, true);
            editor.apply();
        }
    }

    /**
     * Enregistrer l'accessToken dans SharedPreferences.
     *
     * @param accessToken Le token d'accès à enregistrer
     */
    public void saveAccessToken(String accessToken) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.apply(); // Sauvegarde les modifications de manière asynchrone
    }

    /**
     * Récupérer l'accessToken depuis SharedPreferences.
     *
     * @return L'accessToken enregistré, ou null s'il n'existe pas
     */
    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    /**
     * Supprimer l'accessToken de SharedPreferences (exemple : pour déconnexion).
     */
    public void clearAccessToken() {
        editor.remove(KEY_ACCESS_TOKEN);
        editor.apply(); // Sauvegarde les modifications
    }

    /**
     * Vérifie si un accessToken est enregistré.
     *
     * @return true si un accessToken existe, sinon false
     */
    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    /**
     * Vérifie si c'est la première connexion.
     *
     * @return true si c'est la première connexion, sinon false
     */
    public boolean isFirstConnection() {
        return sharedPreferences.getBoolean(KEY_IS_FIRST_CONNECTION, true);
    }

    /**
     * Met à jour la valeur de isFirstConnection.
     *
     * @param isFirst false pour indiquer que ce n'est plus la première connexion
     */
    public void setFirstConnection(boolean isFirst) {
        editor.putBoolean(KEY_IS_FIRST_CONNECTION, isFirst);
        editor.commit();
    }

    public void setId(int id){
        editor.putInt(ID, id);
        editor.commit();
    }

    public int getId(){
        return  sharedPreferences.getInt(ID, 0);
    }
}
