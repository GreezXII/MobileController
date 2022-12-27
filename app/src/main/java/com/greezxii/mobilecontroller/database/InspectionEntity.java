package com.greezxii.mobilecontroller.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public LocalDate paymentDate;
    public BigDecimal debt;
    public LocalDate lastInspectionDate;
    public int value;
    public boolean isAntimagnet;
    public boolean isDisabled;
    public BigDecimal debtByActs;
    public String contacts;
    public LocalDate installationDate;
    public LocalDate verificationDate;
    public int numberOfDigits;
    public String info;

    public void fromString(String str) {
        String[] row = str.split("|");
        id = Integer.parseInt(row[0]);
        // Parse
        street = row[1].split(",")[0];
        // Building
        String buildingRegex = "(?<=дом\\.).*(?= )";
        Pattern pattern = Pattern.compile(buildingRegex);
        String[] buildingNumberLetter = pattern.matcher(row[1]).toString().split("-");
        buildingNumber = Integer.parseInt(buildingNumberLetter[0]);
        buildingLetter = buildingNumberLetter[1].charAt(0);
        // Block
        String blockRegex = "(?<=кор\\.).*";
        pattern = Pattern.compile(buildingRegex);
        String[] blockNumberLetter = pattern.matcher(row[1]).toString().split("-");
        blockNumber = Integer.parseInt(blockNumberLetter[0]);
        blockLetter = blockNumberLetter[1].charAt(0);
        // Appartment
        apartmentNumber = Integer.parseInt(row[3]);
        apartmentLetter = row[4].charAt(0);

        fullName = row[5];
        meterSerialId = row[6];
        meterModel = row[7];
        paymentDate = stringToLocalDate(row[8]);
        debt = new BigDecimal(row[9]);
        lastInspectionDate = stringToLocalDate(row[10]);
        value = Integer.parseInt(row[11]);
        isAntimagnet = Boolean.parseBoolean(row[12]);
        isDisabled = Boolean.parseBoolean(row[13]);
        debtByActs = new BigDecimal(row[14]);
        contacts = row[15];
        installationDate = stringToLocalDate(row[16]);
        verificationDate = stringToLocalDate(row[17]);
        numberOfDigits = Integer.parseInt(row[18]);
    }

    private LocalDate stringToLocalDate(String s) {
        return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
