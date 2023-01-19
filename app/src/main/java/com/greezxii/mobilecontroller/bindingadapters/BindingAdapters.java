package com.greezxii.mobilecontroller.bindingadapters;

import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import com.greezxii.mobilecontroller.database.Inspection;
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
}
