package com.lesadrax.registrationclient.data.repository.db;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lesadrax.registrationclient.from.model.FormValue;

import java.lang.reflect.Type;
import java.util.Map;

public class Converters {

    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromMap(Map<String, FormValue> map) {
        return gson.toJson(map); // Convert the Map to a JSON String
    }

    @TypeConverter
    public static Map<String, FormValue> toMap(String json) {
        Type type = new TypeToken<Map<String, FormValue>>() {}.getType();
        return gson.fromJson(json, type); // Convert the JSON String back to a Map
    }
}
