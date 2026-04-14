package com.lesadrax.registrationclient.application;

import android.app.Application;

import com.lesadrax.registrationclient.data.model.Operation;

import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
    private static MyApplication instance;
    private Operation tempOperation;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public void setTempOperation(Operation operation) {
        this.tempOperation = operation;
    }

    public Operation getTempOperation() {
        Operation operation = this.tempOperation;
        this.tempOperation = null; // Nettoyer après utilisation
        return operation;
    }

    // Version alternative avec gestion de plusieurs objets
    private Map<String, Operation> tempOperationMap = new HashMap<>();

    public void setTempOperation(String key, Operation operation) {
        tempOperationMap.put(key, operation);
    }

    public Operation getTempOperation(String key) {
        Operation operation = tempOperationMap.remove(key);
        return operation;
    }

    // Nettoyer toutes les données temporaires
    public void clearTempData() {
        if (tempOperationMap != null) {
            tempOperationMap.clear();
        }
        tempOperation = null;
    }
}