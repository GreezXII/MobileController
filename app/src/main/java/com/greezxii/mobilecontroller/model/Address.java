package com.greezxii.mobilecontroller.model;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Address {
    public String street;
    public int buildingNumber;
    public String buildingLetter;
    public Integer blockNumber;
    public String blockLetter;
    public Integer apartmentNumber;
    public String apartmentLetter;

    public String getBuildingAddress() {
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

    public String getApartmentAddress() {
        String result = apartmentNumber.toString();
        if(apartmentLetter != null)
            result += apartmentLetter;
        return result;
    }

    public String findBuildingLetter(String s) {
        // Соответствует букве дома. Поиск по префиксу "дом.N-" где N - это номер дома из 1-3 цифр
        String regex = "((?<=дом\\.[0-9][0-9][0-9]-)|(?<=дом\\.[0-9][0-9]-)|(?<=дом\\.[0-9]-))[а-яА-Я]";
        return findMatch(s, regex);
    }

    public String findBlockLetter(String s) {
        // Соответствует букве корпуса. Поиск по префиксу "кор.N-" где N - это номер корпуса из 1-2 цифр
        String regex = "((?<=кор\\.[0-9][0-9]-)|(?<=кор\\.[0-9]-))[а-яА-Я]";
        return findMatch(s, regex);
    }

    public Integer findBlockNumber(String s) {
        // Соответствует номеру корпуса. "кор."
        String regex = "(?<=кор\\.)\\d*";
        String match = findMatch(s, regex);
        if (match != null)
            return Integer.parseInt(match);
        else
            return null;
    }

    public String findMatch(String s, String regex) {
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
