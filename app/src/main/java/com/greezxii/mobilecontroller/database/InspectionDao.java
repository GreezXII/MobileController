package com.greezxii.mobilecontroller.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface InspectionDao {
    @Insert
    void insertAll(Inspection... inspections);

    @Delete
    void deleteAll(Inspection... inspections);

    @Update
    void updateInspection(Inspection inspection);

    @Query("SELECT * FROM Inspection")
    List<Inspection> getAllInspections();

    @Query("SELECT * FROM Inspection WHERE id = :id")
    Inspection getInspectionById(int id);

    @Query("SELECT Count(*) FROM Inspection WHERE value >= 0")
    Integer getPerformedInspectionsCount();
}
