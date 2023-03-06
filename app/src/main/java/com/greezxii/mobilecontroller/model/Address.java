package com.greezxii.mobilecontroller.model;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlinx.coroutines.channels.ActorKt;

public class Address {
    public String street;
    public int buildingNumber;
    public String buildingLetter;
    public Integer blockNumber;
    public String blockLetter;
    public Integer apartmentNumber;
    public String apartmentLetter;

    @NonNull
    @Override
    public String toString() {
        return getBuildingAddress();
    }

    public String getBuildingAddress() {
        StringBuilder result = new StringBuilder();
        Locale locale = new Locale("ru", "RU");
        if(street != null)
            result.append(String.format(locale, "%s, ", street));
        if(buildingNumber != 0)
            result.append(String.format(locale, "дом.%d", buildingNumber));
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
}
