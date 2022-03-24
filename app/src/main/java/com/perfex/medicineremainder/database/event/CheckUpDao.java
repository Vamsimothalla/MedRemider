package com.perfex.medicineremainder.database.event;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.perfex.medicineremainder.model.CheckUp;

import java.util.List;

@Dao
public interface CheckUpDao {
    @Query("SELECT * FROM checkup")
    List<CheckUp> getAll();

    @Query("SELECT * FROM checkup where id=:id")
    CheckUp getById(Long id);

    @Insert
    void insert(CheckUp checkUp);

    @Update
    void update(CheckUp checkUp);

    @Delete
    void delete(CheckUp checkUp);
}
