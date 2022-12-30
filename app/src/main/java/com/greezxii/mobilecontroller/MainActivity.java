package com.greezxii.mobilecontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import com.greezxii.mobilecontroller.database.InspectionEntity;
import com.greezxii.mobilecontroller.recycler.InspectionsRecyclerAdapter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initButtons();

        DataManager dm = new DataManager();
        ArrayList<InspectionEntity> inspections = dm.loadEntities();
        initRecycler(inspections);
    }

    private void initRecycler(List<InspectionEntity> data) {
        RecyclerView recyclerView = findViewById(R.id.recycler_addresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new InspectionsRecyclerAdapter(getApplicationContext(), data));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, RecyclerView.VERTICAL);
        Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.divider_drawable, null);
        if(divider != null)
            dividerItemDecoration.setDrawable(divider);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void initButtons()
    {
        Button button;
        // Settings button
        button = findViewById(R.id.button_settings);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        // Open info button
        button = findViewById(R.id.button_openInfo);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(intent);
        });
    }
}