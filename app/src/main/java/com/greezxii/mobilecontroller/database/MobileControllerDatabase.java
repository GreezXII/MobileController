package com.greezxii.mobilecontroller.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 1, entities = {Inspection.class})
public abstract class MobileControllerDatabase extends RoomDatabase {
    public abstract InspectionDao inspectionDao();
    private static MobileControllerDatabase INSTANCE;
    public static MobileControllerDatabase getDatabase(Context context) {
        if(INSTANCE == null)
        {
            INSTANCE = Room.databaseBuilder(context, MobileControllerDatabase.class, "inspections.db").build();
        }
        return INSTANCE;
    }
}