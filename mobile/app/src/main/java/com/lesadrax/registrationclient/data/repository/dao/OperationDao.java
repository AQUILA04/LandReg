package com.lesadrax.registrationclient.data.repository.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lesadrax.registrationclient.data.model.Operation;

import java.util.List;

@Dao
public interface OperationDao {

    @Insert
    long insertOperation(Operation data);

    @Update
    int updateOperation(Operation data);

    @Delete
    int deleteOperation(Operation data);

    @Query("SELECT * FROM operations WHERE synced =0")
    List<Operation> getAllOperations();


    @Query("SELECT * FROM operations WHERE isCompleted =1 AND synced =0")
    List<Operation> getAllOperationsCompleted();



    @Query("SELECT * FROM operations WHERE id = :id")
    Operation getOperationById(long id);

}
