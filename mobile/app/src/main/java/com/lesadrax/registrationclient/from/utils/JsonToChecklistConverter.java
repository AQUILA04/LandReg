package com.lesadrax.registrationclient.from.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.lesadrax.registrationclient.data.model.Checklist;

import org.json.JSONObject;

import java.lang.reflect.Type;

public class JsonToChecklistConverter {

    // Convertir une chaîne JSON en objet Checklist
    public static Checklist fromJson(JSONObject jsonObject) {
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.toString(), Checklist.class);
    }

    // Alternative: Si le JSON est directement l'objet Checklist
    public static Checklist fromJsonDirect(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Checklist.class);
    }

}
