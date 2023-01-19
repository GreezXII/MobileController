package com.greezxii.mobilecontroller.bindingadapters;

import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class BindingAdapters {
    @BindingAdapter("date")
    public static void setDate(TextView view, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ru"));
        view.setText(formatter.format(date));
    }
}
