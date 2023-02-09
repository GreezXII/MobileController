package com.greezxii.mobilecontroller.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.greezxii.mobilecontroller.database.dbconverters.BigDecimalConverter;
import com.greezxii.mobilecontroller.database.dbconverters.LocalDateConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@TypeConverters({LocalDateConverter.class, BigDecimalConverter.class})
public class Inspection {
    @PrimaryKey
    public int id;
    public String street;
    public int buildingNumber;
    public String buildingLetter;
    public Integer blockNumber;
    public String blockLetter;
    public Integer apartmentNumber;
    public String apartmentLetter;
    public String fullName;
    public String meterSerialId;
    public String meterModel;
    public LocalDate paymentDate;
    public BigDecimal debt;
    public LocalDate lastInspectionDate;
    public Boolean isAntimagnet;
    public Boolean isDisabled;
    public BigDecimal debtByActs;
    public String contacts;
    public LocalDate installationDate;
    public LocalDate verificationDate;
    public int numberOfDigits;
    public String info;
    public String note;
    public Integer value;

    public void fromString(@NonNull String s) {
        String[] row = s.split("[|]");

        id = Integer.parseInt(row[0].trim());
        street = row[1].split(",")[0];
        buildingNumber = Integer.parseInt(row[2].trim());
        buildingLetter = findBuildingLetter(row[1]);
        blockNumber = findBlockNumber(row[1]);
        blockLetter = findBlockLetter(row[1]);
        apartmentNumber = Integer.parseInt(row[3].trim());
        apartmentLetter = row[4];
        fullName = row[5];
        meterSerialId = row[6];
        meterModel = row[7];
        paymentDate = stringToLocalDate(row[8]);
        debt = new BigDecimal(row[9]);
        lastInspectionDate = stringToLocalDate(row[10]);
        //value = Integer.parseInt(row[11].trim());  //TODO: Remove initializing of value?
        isAntimagnet = Boolean.parseBoolean(row[12]);
        isDisabled = Boolean.parseBoolean(row[13]);
        debtByActs = new BigDecimal(row[14]);
        contacts = row[15].trim();
        installationDate = stringToLocalDate(row[16]);
        verificationDate = stringToLocalDate(row[17]);
        numberOfDigits = Integer.parseInt(row[18].trim());
    }

    @NonNull
    public String toString() {
        StringJoiner joiner = new StringJoiner("|");
        joiner.add(String.valueOf(id));
        joiner.add(getAddress());
        joiner.add(getApartment());
        joiner.add(fullName);
        joiner.add(meterSerialId);
        joiner.add(meterModel);
        joiner.add(localDateToString(paymentDate));
        joiner.add(debt.toString());
        joiner.add(localDateToString(lastInspectionDate));
        joiner.add(value.toString());
        String isAntimagnetStr = isAntimagnet ? "да" : "нет";
        joiner.add(isAntimagnetStr);
        String isDisabledStr = isDisabled ? "да" : "нет";
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
        // Соответствует номеру корпуса. "кор."
        String regex = "(?<=кор\\.)\\d*";
        String match = findMatch(s, regex);
        if (match != null)
            return Integer.parseInt(match);
        else
            return null;
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

    @NonNull
    public String getAddress() {
        StringBuilder result = new StringBuilder();
        Locale locale = new Locale("ru", "RU");
        result.append(String.format(locale, "%s, дом.%d", street, buildingNumber));
        if(buildingLetter != null)
            result.append(String.format(locale, "-%s", buildingLetter));
        if(blockNumber != null)
            result.append(String.format(locale, " кор.%d", blockNumber));
        if(blockLetter != null)
            result.append(String.format(locale, "-%s", blockLetter));
        return result.toString();
    }

    public String getApartment() {
        String result = apartmentNumber.toString();
        if(apartmentLetter != null)
            result += apartmentLetter;
        return result;
    }

}
