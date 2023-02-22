package com.greezxii.mobilecontroller.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.greezxii.mobilecontroller.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView info = findViewById(R.id.textView_info);
        info.setMovementMethod(new ScrollingMovementMethod());
    }
}