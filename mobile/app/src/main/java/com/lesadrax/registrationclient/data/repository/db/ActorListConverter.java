package com.lesadrax.registrationclient.data.repository.db;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lesadrax.registrationclient.data.model.ActorModel;
import java.lang.reflect.Type;
import java.util.List;

public class ActorListConverter {

    @TypeConverter
    public static String fromActorList(List<ActorModel> actors) {
        if (actors == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<ActorModel>>() {}.getType();
        return gson.toJson(actors, type);
    }

    @TypeConverter
    public static List<ActorModel> toActorList(String actorListString) {
        if (actorListString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<ActorModel>>() {}.getType();
        return gson.fromJson(actorListString, type);
    }
}
