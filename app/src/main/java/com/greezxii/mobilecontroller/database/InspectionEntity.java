package com.greezxii.mobilecontroller.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
public class InspectionEntity {
    @PrimaryKey
    public int id;
    public String street;
    public int buildingNumber;
    public String buildingLetter;
    public Integer blockNumber; // Номер корпуса может отсутствовать, поэтому используется ссылочный тип Integer
    public String blockLetter;
    public int apartmentNumber;
    public String apartmentLetter;
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

    public void fromString(String s) {
        String[] row = s.split("|");

        id = Integer.parseInt(row[0]);
        street = row[1].split(",")[0];
        buildingNumber = Integer.parseInt(row[2]);
        buildingLetter = findBuildingLetter(row[1]);
        blockNumber = findBlockNumber(row[1]);
        blockLetter = findBlockLetter(row[1]);
        apartmentNumber = Integer.parseInt(row[3]);
        apartmentLetter = row[4];
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
    private String findBuildingLetter(String s) {
        // Соответствует букве дома. Поиск по префиксу "дом.N-" где N - это номер дома из 1-3 цифр
        String regex = "((?<=дом\\.[0-9][0-9][0-9]-)|(?<=дом\\.[0-9][0-9]-)|(?<=дом\\.[0-9]-))[а-яА-Я]";
        return findMatch(s, regex);
    }
    private String findBlockLetter(String s) {
        // Соответствует букве корпуса. Поиск по префиксу "кор.N-" где N - это номер корпуса из 1-2 цифр
        String regex = "((?<=кор\\.[0-9][0-9]-)|(?<=кор\\.[0-9]-))[а-яА-Я]";
        return findMatch(s, regex);
    }
    private Integer findBlockNumber(String s) {
        String regex = "(?<=кор\\.)\\d*";
        return Integer.parseInt(findMatch(s, regex));
    }
    private String findMatch(String s, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()) {
            return matcher.group();
        }
        else {
            return null;
        }
    }
}
