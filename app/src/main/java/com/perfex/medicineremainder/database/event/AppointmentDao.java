package com.perfex.medicineremainder.database.event;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.perfex.medicineremainder.model.Appointment;

import java.util.List;

@Dao
public interface AppointmentDao {
    @Query("SELECT * FROM appointment")
    List<Appointment> getAll();

    @Query("SELECT * FROM appointment where id=:id")
    Appointment getById(Long id);

    @Insert
    void insert(Appointment appointment);

    @Update
    void update(Appointment appointment);
    @Delete
    void delete(Appointment appointment);
}
