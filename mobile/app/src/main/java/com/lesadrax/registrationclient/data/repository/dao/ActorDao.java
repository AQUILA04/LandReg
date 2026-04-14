package com.lesadrax.registrationclient.data.repository.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lesadrax.registrationclient.data.model.Actor;

import java.util.List;

@Dao
public interface ActorDao {

    @Insert
    long insertActor(Actor actor);

    @Update
    int updateActor(Actor actor);

    @Delete
    int deleteActor(Actor actor);

    @Query("SELECT * FROM actors WHERE synced == 0")
    List<Actor> getAllActors();

    @Query("SELECT * FROM actors WHERE person == 1")
    List<Actor> getAllPersonActors();

    @Query("SELECT * FROM actors WHERE id = :id")
    Actor getActorById(long id);

}
