package com.greezxii.mobilecontroller.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.greezxii.mobilecontroller.model.Card;

@Database(version = 1, entities = {Card.class})
public abstract class MobileControllerDatabase extends RoomDatabase {
    public abstract CardDao cardsDao();
    private static MobileControllerDatabase INSTANCE;
    public static MobileControllerDatabase getDatabase(Context context) {
        if(INSTANCE == null)
        {
            INSTANCE = Room.databaseBuilder(context, MobileControllerDatabase.class, "cards.db").build();
        }
        return INSTANCE;
    }
}