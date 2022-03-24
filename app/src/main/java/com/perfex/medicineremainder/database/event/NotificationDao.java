package com.perfex.medicineremainder.database.event;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.perfex.medicineremainder.model.CheckUp;
import com.perfex.medicineremainder.model.Notification;

import java.util.List;

@Dao
public interface NotificationDao {
    @Query("SELECT * FROM notification")
    List<Notification> getAll();

    @Query("SELECT * FROM notification where id=:id")
    CheckUp getById(Long id);

    @Insert
    void insert(Notification notification);

    @Update
    void update(Notification notification);

    @Delete
    void delete(Notification notification);
}
