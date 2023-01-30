package com.greezxii.mobilecontroller;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greezxii.mobilecontroller.database.Inspection;
import com.greezxii.mobilecontroller.databinding.ActivityMainBinding;
import com.greezxii.mobilecontroller.recycler.InspectionsRecyclerAdapter;
import com.greezxii.mobilecontroller.repository.DataRepository;
import com.greezxii.mobilecontroller.viewmodel.MainViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    MainViewModel vm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        initButtons();
        initRecycler(vm.inspections);
        //vm.deleteInspections();
    }

    @Override
    protected void onPause() {
        vm.updateSelectedInspection();
        super.onPause();
    }

    private void initViewModel() {
        DataRepository repository = new DataRepository(this);
        vm = new MainViewModel(repository);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
        binding.setVm(vm);
    }

    private void initRecycler(List<Inspection> data) {
        RecyclerView recyclerView = findViewById(R.id.recycler_addresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        InspectionsRecyclerAdapter.OnInspectionClickListener clickListener = (position) -> vm.onSelect(position);
        InspectionsRecyclerAdapter adapter = new InspectionsRecyclerAdapter(getApplicationContext(), data, clickListener);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, RecyclerView.VERTICAL);
        Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.divider_drawable, null);
        if(divider != null)
            dividerItemDecoration.setDrawable(divider);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void initButtons() {
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