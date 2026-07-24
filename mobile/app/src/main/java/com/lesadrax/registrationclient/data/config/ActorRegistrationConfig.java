package com.lesadrax.registrationclient.data.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import com.lesadrax.registrationclient.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Active ou désactive l'API v2 multipart pour la création d'acteurs.
 * Priorité : SharedPreferences &gt; fichier assets &gt; BuildConfig.
 */
public class ActorRegistrationConfig {

    private static final String PREF_NAME = "registration_config";
    private static final String KEY_USE_V2 = "actor_registration_use_v2";
    private static final String ASSET_FILE = "registration_config.properties";
    private static final String PROP_V2_ENABLED = "actor.registration.v2.enabled";

    private final boolean v2Enabled;

    public ActorRegistrationConfig(Context context) {
        boolean assetDefault = loadFromAssets(context.getAssets());
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (preferences.contains(KEY_USE_V2)) {
            v2Enabled = preferences.getBoolean(KEY_USE_V2, assetDefault);
        } else {
            v2Enabled = assetDefault;
        }
    }

    public boolean isV2Enabled() {
        return v2Enabled;
    }

    public static void setV2Enabled(Context context, boolean enabled) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_USE_V2, enabled)
            .apply();
    }

    private static boolean loadFromAssets(AssetManager assetManager) {
        Properties properties = new Properties();
        try (InputStream inputStream = assetManager.open(ASSET_FILE)) {
            properties.load(inputStream);
            return Boolean.parseBoolean(properties.getProperty(PROP_V2_ENABLED, "false"));
        } catch (IOException e) {
            return BuildConfig.USE_ACTOR_REGISTRATION_V2;
        }
    }
}
