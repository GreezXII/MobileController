package com.greezxii.mobilecontroller.database.converters;

import androidx.room.TypeConverter;
import java.math.BigDecimal;

public class BigDecimalConverter {
    @TypeConverter
    public static String bigDecimalToString(BigDecimal value) {
        return value == null ? null : value.toString();
    }

    @TypeConverter
    public static BigDecimal stringToBigDecimal(String value) {
        return value == null ? BigDecimal.ZERO : new BigDecimal(value);
    }
}
