package com.perfex.medicineremainder.database.event;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.perfex.medicineremainder.model.Note;
import com.perfex.medicineremainder.model.Refill;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM note")
    List<Note> getAll();

    @Query("SELECT * FROM note where id=:id")
    Note getById(Long id);

    @Insert
    void insert(Note note);

    @Delete
    void delete(Note note);
}
