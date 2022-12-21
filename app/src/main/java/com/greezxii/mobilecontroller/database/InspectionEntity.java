package com.greezxii.mobilecontroller.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class InspectionEntity {
    @PrimaryKey
    public int id;
    public String street;
    public int buildingNumber;
    public char buildingLetter;
    public int blockNumber;
    public char blockLetter;
    public int apartmentNumber;
    public char apartmentLetter;
    public String fullName;
    public String meterSerialId;
    public String meterModel;
    public Date paymentDate;
    public BigDecimal debt;
    public Date lastInspectionDate;
    public int value;
    public boolean isAntimagnet;
    public boolean isDisabled;
    public BigDecimal debtByActs;
    public String contacts;
    public Date installationDate;
    public Date vefiricationDate;
    public int lastValue;
    public String info;
}
