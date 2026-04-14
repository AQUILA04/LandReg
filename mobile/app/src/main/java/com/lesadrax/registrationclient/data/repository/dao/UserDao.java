package com.lesadrax.registrationclient.data.repository.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lesadrax.registrationclient.data.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insertUser(User user);

    @Query("SELECT * FROM user")
    List<User> getUsers();

    @Insert
    void insertUsers(List<User> users);

    @Query("SELECT * FROM user WHERE username = :username")
    User getUserByUsername(String username);
}
