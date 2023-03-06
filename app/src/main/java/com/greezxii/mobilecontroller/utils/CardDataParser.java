package com.greezxii.mobilecontroller.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardDataParser {
    public static String findBuildingLetter(String s) {
        // Соответствует букве дома. Поиск по префиксу "дом.N-" где N - это номер дома из 1-3 цифр
        String regex = "((?<=дом\\.[0-9][0-9][0-9]-)|(?<=дом\\.[0-9][0-9]-)|(?<=дом\\.[0-9]-))[а-яА-Я]";
        return findMatch(s, regex);
    }

    public static String findBlockLetter(String s) {
        // Соответствует букве корпуса. Поиск по префиксу "кор.N-" где N - это номер корпуса из 1-2 цифр
        String regex = "((?<=кор\\.[0-9][0-9]-)|(?<=кор\\.[0-9]-))[а-яА-Я]";
        return findMatch(s, regex);
    }

    public static Integer findBlockNumber(String s) {
        // Соответствует номеру корпуса. "кор."
        String regex = "(?<=кор\\.)\\d*";
        String match = findMatch(s, regex);
        if (match != null)
            return Integer.parseInt(match);
        else
            return null;
    }

    public static Boolean findTroublesome(int id, String s) {
        String regex = "(V I P)[#\\s]+" + id;
        String match = findMatch(s, regex);
        return match != null;
    }

    public static String findNote(int id, String s) {
        String regex = "(^ {4})" + id + "(.*?)(?=={5}|\\Z)";
        return findMatch(s, regex);
    }

    public static String findMatch(String s, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()) {
            return matcher.group();
        }
        else {
            return null;
        }
    }
}
