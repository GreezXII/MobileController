package com.greezxii.mobilecontroller.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(version = 1, entities = {InspectionEntity.class})
abstract class MobileControllerDatabase extends RoomDatabase {
    public abstract InspectionDao inspectionDao();
}