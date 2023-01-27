package com.greezxii.mobilecontroller.bindingadapters;

import android.graphics.Color;
import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseMethod;

import com.greezxii.mobilecontroller.database.Inspection;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class BindingAdapters {

    @BindingAdapter("date")
    public static void setDate(TextView view, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ru"));
        view.setText(formatter.format(date));
    }

    @BindingAdapter("meter")
    public static void setMeter(TextView view, Inspection inspection) {
        String result = String.format(new Locale("ru"),
                "%s  № %s [%d]",
                inspection.meterModel,
                inspection.meterSerialId,
                inspection.numberOfDigits);
        view.setText(result);
    }

    @BindingAdapter("booleanValue")
    public static void setBoolean(TextView view, boolean value) {
        if (value)
            view.setText("да");
        else
            view.setText("нет");
    }

    @BindingAdapter("coloredValue")
    public static void setColored(TextView view, BigDecimal value) {
        BigDecimal zero = new BigDecimal(0);
        if (value.compareTo(zero) < 0)
            view.setTextColor(Color.RED);
        else
            view.setTextColor(Color.BLACK);
        view.setText(String.format(new Locale("ru"), "%.2f", value));
    }

    @BindingAdapter("debtByActs")
    public static void setDebtByActs(TextView view, BigDecimal value) {
        BigDecimal zero = new BigDecimal(0);
        if (value.compareTo(zero) == 0)
            view.setText("нет");
        else
            view.setText(String.format(new Locale("ru"), "%.2f", value));
    }

    @InverseMethod("stringToInt")
    public static String intToString(int value) {
        return String.valueOf(value);
    }
    public static int stringToInt(String value) { return Integer.parseInt(value); }
}
