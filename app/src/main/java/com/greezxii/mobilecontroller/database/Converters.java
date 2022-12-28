package com.greezxii.mobilecontroller.database;

import androidx.room.TypeConverter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public class Converters {
    @TypeConverter
    public static LocalDate timestampToLcalDate(Long value) {
        return value == null ? null : LocalDate.ofEpochDay(value);
    }

    @TypeConverter
    public static Long localDateToTimestamp(LocalDate date) {
        return date == null ? null : date.toEpochDay();
    }

    @TypeConverter
    public static String bigDecimalToString(BigDecimal value) {
        return value == null ? null : value.toString();
    }

    @TypeConverter
    public static BigDecimal stringToBigDecimal(String value) {
        return value == null ? BigDecimal.ZERO : new BigDecimal(value);
    }
}
