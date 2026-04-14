package com.lesadrax.registrationclient.data.repository.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.data.model.User;
import com.lesadrax.registrationclient.data.repository.dao.ActorDao;
import com.lesadrax.registrationclient.data.repository.dao.OperationDao;
import com.lesadrax.registrationclient.data.repository.dao.UserDao;

@Database(entities = {Actor.class, Operation.class, User.class}, version = 11)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ActorDao actorDao();
    public abstract OperationDao operationDao();
    public abstract UserDao userDao();
}
