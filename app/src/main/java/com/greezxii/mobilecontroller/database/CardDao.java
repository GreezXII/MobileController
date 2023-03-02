package com.greezxii.mobilecontroller.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.greezxii.mobilecontroller.model.Address;
import com.greezxii.mobilecontroller.model.Card;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

@Dao
public interface CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Card... cards);

    @Delete
    void deleteAll(Card... cards);

    @Update
    void updateCard(Card card);

    @Query("SELECT * FROM Card ORDER BY " +
            "street, buildingNumber, buildingLetter, " +
            "blockNumber, blockLetter, " +
            "apartmentNumber, apartmentLetter")
    List<Card> getAllCards();

    @Query("SELECT * FROM Card " +
            "WHERE street is :street AND " +
            "buildingNumber is :buildingNumber AND " +
            "buildingLetter is :buildingLetter AND " +
            "blockNumber is :blockNumber AND " +
            "blockLetter is :blockLetter " +
            "ORDER BY " +
            "street, buildingNumber, buildingLetter, " +
            "blockNumber, blockLetter, " +
            "apartmentNumber, apartmentLetter")
    List<Card> getCardsByBuildingAddress(String street,
                                         int buildingNumber,
                                         String buildingLetter,
                                         Integer blockNumber,
                                         String blockLetter);

    @Query("SELECT DISTINCT street, buildingNumber, buildingLetter, blockNumber, blockLetter " +
            "FROM Card ORDER BY street, buildingNumber, buildingLetter, blockNumber, blockLetter")
    List<Address> getDistinctBuildingAddresses();

    @Query("SELECT Count(*) FROM Card WHERE consumption >= 0")
    Integer getPerformedInspectionsCount();
}
