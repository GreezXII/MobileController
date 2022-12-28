package com.greezxii.mobilecontroller.database.converters;

import androidx.room.TypeConverter;
import java.time.LocalDate;

public class LocalDateConverter {
    @TypeConverter
    public static LocalDate timestampToLocalDate(Long value) {
        return value == null ? null : LocalDate.ofEpochDay(value);
    }

    @TypeConverter
    public static Long localDateToTimestamp(LocalDate date) {
        return date == null ? null : date.toEpochDay();
    }
}
