package com.greezxii.mobilecontroller.bindingadapters;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;
import androidx.databinding.InverseMethod;

import com.greezxii.mobilecontroller.model.Card;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class BindingAdapters {

    @BindingAdapter("contract")
    public static void setContract(TextView view, int value) {
        if (value == 0)
            view.setText("");
        else
            view.setText(String.format(new Locale("ru"), "%d", value));
    }

    @BindingAdapter("date")
    public static void setDate(TextView view, LocalDate date) {
        if (date == null)
            view.setText("");
        else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ru"));
            view.setText(formatter.format(date));
        }
    }

    @BindingAdapter("meter")
    public static void setMeter(TextView view, Card card) {
        if (card == null)
            view.setText("");
        else {
            String result = String.format(new Locale("ru"),
                    "%s  № %s [%d]",
                    card.meter.model,
                    card.meter.serialId,
                    card.meter.numberOfDigits);
            view.setText(result);
        }
    }

    @BindingAdapter("booleanValue")
    public static void setBoolean(TextView view, Boolean value) {
        if (value == null)
            view.setText("");
        else if (value)
            view.setText("да");
        else
            view.setText("нет");
    }

    @BindingAdapter("coloredDecimalValue")
    public static void setColored(TextView view, BigDecimal value) {
        if (value == null) {
            view.setText("");
            return;
        }
        BigDecimal zero = new BigDecimal(0);
        if (value.compareTo(zero) < 0)
            view.setTextColor(Color.RED);
        else
            view.setTextColor(Color.BLACK);
        if (value.compareTo(zero) == 0)
            view.setText("нет");
        else
            view.setText(String.format(new Locale("ru"), "%.2f", value));
    }

    @InverseMethod("stringToNullableInteger")
    public static String nullableIntegerToString(Integer value) {
        return value == null ? "" : value.toString();
    }

    public static Integer stringToNullableInteger(String value) {
        return value.equals("") ? null : Integer.parseInt(value);
    }

    public static int booleanToVisibility(Boolean value) {
        return value != null && value ? View.VISIBLE : View.INVISIBLE;
    }
}
