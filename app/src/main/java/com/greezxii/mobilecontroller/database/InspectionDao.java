package com.greezxii.mobilecontroller.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.greezxii.mobilecontroller.model.Inspection;

import java.util.List;

@Dao
public interface InspectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Inspection... inspections);

    @Delete
    void deleteAll(Inspection... inspections);

    @Update
    void updateInspection(Inspection inspection);

    @Query("SELECT * FROM Inspection ORDER BY " +
            "street, buildingNumber, buildingLetter, " +
            "blockNumber, blockLetter, " +
            "apartmentNumber, apartmentLetter")
    List<Inspection> getAllInspections();

    @Query("SELECT Count(*) FROM Inspection WHERE value >= 0")
    Integer getPerformedInspectionsCount();
}
