package com.greezxii.mobilecontroller.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InspectionDao {
    @Insert
    void insertAll(InspectionEntity... users);

    @Delete
    void deleteAll(InspectionEntity... users);

    @Query("SELECT * FROM InspectionEntity")
    List<InspectionEntity> getAllInspections();

    @Query("SELECT * FROM InspectionEntity WHERE id = :id")
    InspectionEntity getInspectionById(int id);
}
