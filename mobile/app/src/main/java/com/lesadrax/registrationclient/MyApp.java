package com.lesadrax.registrationclient;

import android.app.Application;
import android.os.AsyncTask;

import androidx.room.Room;

import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.data.repository.db.AppDatabase;
import com.lesadrax.registrationclient.from.model.ItemData;

import java.util.HashMap;
import java.util.Map;

public class MyApp extends Application {

    private static AppDatabase database;
    private static MyApp instance;  // ← Ajouter l'instance

    // Données temporaires pour la navigation
    private Operation tempOperation;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;  // ← Initialiser l'instance

        database = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "database")
                .fallbackToDestructiveMigration()
                .build();

        ItemData.initData();
    }

    // Getter pour l'instance
    public static MyApp getInstance() {
        return instance;
    }

    // Getter pour la base de données
    public static AppDatabase getDatabase() {
        return database;
    }

    private Map<String, Operation> tempOperationMap = new HashMap<>();

    public void setTempOperation(String key, Operation operation) {
        tempOperationMap.put(key, operation);
    }


    public void setTempOperation(Operation operation) {
        tempOperation = operation;
    }

    // Gestion des opérations temporaires


    public Operation getTempOperation() {
        return this.tempOperation;
    }

    public void clearTempData() {
        if (tempOperationMap != null) {
            tempOperationMap.clear();
        }
        tempOperation = null;
    }
}