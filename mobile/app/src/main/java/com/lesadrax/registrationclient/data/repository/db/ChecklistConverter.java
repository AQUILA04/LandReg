package com.lesadrax.registrationclient.data.repository.db;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.lesadrax.registrationclient.data.model.Checklist;

public class ChecklistConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static Checklist fromString(String value) {
        return gson.fromJson(value, Checklist.class);
    }

    @TypeConverter
    public static String fromChecklist(Checklist checklist) {
        return gson.toJson(checklist);
    }
}
