package com.greezxii.mobilecontroller.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.greezxii.mobilecontroller.database.dbconverters.BigDecimalConverter;
import com.greezxii.mobilecontroller.database.dbconverters.LocalDateConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@TypeConverters({LocalDateConverter.class, BigDecimalConverter.class})
public class Inspection {
    // Ссылочный тип Integer используется для полей, которые
    // могут принимать целочисленные значения и null
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
    public boolean isAntimagnet;
    public boolean isDisabled;
    public BigDecimal debtByActs;
    public String contacts;
    public LocalDate installationDate;
    public LocalDate verificationDate;
    public int numberOfDigits;
    public String info;
    public String note;
    public Integer value;
    // Поле для отслеживания изменений, true - пользователь внёс изменения, иначе false.
    // Не записывается в базу данных
    @Ignore
    public boolean isChanged;

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

    private LocalDate stringToLocalDate(String s) {
        return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd.MM.yy"));
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
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Locale locale = new Locale("ru", "RU");
        result.append(String.format(locale, "%s, дом.%d", street, buildingNumber));
        if(buildingLetter != null)
            result.append(String.format(locale, "-%s", buildingLetter));
        if(blockNumber != null)
            result.append(String.format(locale, " кор.%d", blockNumber));
        if(blockLetter != null)
            result.append(String.format(locale, "-%s", blockLetter));
        result.append(String.format(locale, ", %d", apartmentNumber));
        if(apartmentLetter != null)
            result.append(apartmentLetter);
        return result.toString();
    }

}
