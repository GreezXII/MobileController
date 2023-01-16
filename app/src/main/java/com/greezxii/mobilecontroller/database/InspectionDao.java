package com.greezxii.mobilecontroller.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InspectionDao {
    @Insert
    void insertAll(Inspection... users);

    @Delete
    void deleteAll(Inspection... users);

    @Query("SELECT * FROM Inspection")
    List<Inspection> getAllInspections();

    @Query("SELECT * FROM Inspection WHERE id = :id")
    Inspection getInspectionById(int id);
}
