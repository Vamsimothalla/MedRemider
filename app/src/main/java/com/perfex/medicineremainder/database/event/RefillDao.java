package com.perfex.medicineremainder.database.event;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.perfex.medicineremainder.model.Refill;

import java.util.List;

@Dao
public interface RefillDao {
    @Query("SELECT * FROM refill")
    List<Refill> getAll();

    @Query("SELECT * FROM refill where id=:id")
    Refill getById(Long id);

    @Insert
    void insert(Refill refill);

    @Delete
    void delete(Refill refill);

    @Update
    void update(Refill refill);
}
