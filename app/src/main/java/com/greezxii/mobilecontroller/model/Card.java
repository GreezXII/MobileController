package com.greezxii.mobilecontroller.model;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.greezxii.mobilecontroller.database.dbconverters.BigDecimalConverter;
import com.greezxii.mobilecontroller.database.dbconverters.LocalDateConverter;

import org.checkerframework.checker.units.qual.A;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

@Entity
@TypeConverters({LocalDateConverter.class, BigDecimalConverter.class})
public class Card {
    @PrimaryKey
    public int id;
    public String name;
    public String contacts;
    public String info;
    public String note;
    public LocalDate paymentDate;
    public LocalDate lastInspectionDate;
    public BigDecimal debt;
    public BigDecimal debtByActs;
    public Integer consumption;
    public Boolean isTroublesome;
    @Embedded
    public Address address;
    @Embedded
    public ElectricityMeter meter;

    public void fromString(@NonNull String s) {
        String[] row = s.split("[|]");

        id = Integer.parseInt(row[0].trim());
        name = row[5];
        paymentDate = stringToLocalDate(row[8]);
        debt = new BigDecimal(row[9]);
        lastInspectionDate = stringToLocalDate(row[10]);
        debtByActs = new BigDecimal(row[14]);
        contacts = row[15].trim();

        address = new Address();
        address.street = row[1].split(",")[0];
        address.buildingNumber = Integer.parseInt(row[2].trim());
        address.buildingLetter = address.findBuildingLetter(row[1]);
        address.blockNumber = address.findBlockNumber(row[1]);
        address.blockLetter = address.findBlockLetter(row[1]);
        address.apartmentNumber = Integer.parseInt(row[3].trim());
        address.apartmentLetter = row[4];

        meter = new ElectricityMeter();
        meter.serialId = row[6];
        meter.model = row[7];
        meter.isAntimagnet = Boolean.parseBoolean(row[12]);
        meter.isDisabled = Boolean.parseBoolean(row[13]);
        meter.installationDate = stringToLocalDate(row[16]);
        meter.verificationDate = stringToLocalDate(row[17]);
        meter.numberOfDigits = Integer.parseInt(row[18].trim());
    }

    @NonNull
    public String toString() {
        StringJoiner joiner = new StringJoiner("|");
        joiner.add(String.valueOf(id));
        joiner.add(address.getBuildingAddress());
        joiner.add(address.getApartmentAddress());
        joiner.add(name);
        joiner.add(meter.serialId);
        joiner.add(meter.model);
        joiner.add(localDateToString(paymentDate));
        joiner.add(debt.toString());
        joiner.add(localDateToString(lastInspectionDate));
        joiner.add(consumption.toString());
        String isAntimagnetStr = meter.isAntimagnet ? "да" : "нет";
        joiner.add(isAntimagnetStr);
        String isDisabledStr = meter.isDisabled ? "да" : "нет";
        joiner.add(isDisabledStr);
        //TODO: Add Longtitude and latitude
        String noteStr = note == null ? "" : note;
        return joiner + noteStr + "\r\n";
    }

    private LocalDate stringToLocalDate(String s) {
        return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd.MM.yy"));
    }

    private String localDateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        return formatter.format(date);
    }
}
