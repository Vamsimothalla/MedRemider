package com.perfex.medicineremainder.database.user.medicine;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MedicineDao {
    @Query("select * from Medicine")
    List<Medicine> loadAllMeds();
    @Query("select * from Medicine where id = :id")
    Medicine loadMedById(int id);

    @Query("select * from Medicine where medicineName = :medicineName")
    Medicine loadMedByName(String medicineName);
    @Insert()
    void insert(Medicine medicine);

    @Query("DELETE FROM medicine")
    void deleteAll();
    @Update
    void update(Medicine medicine);
}
