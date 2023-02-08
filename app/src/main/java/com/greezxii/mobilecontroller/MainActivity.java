package com.greezxii.mobilecontroller;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.greezxii.mobilecontroller.database.Inspection;
import com.greezxii.mobilecontroller.databinding.ActivityMainBinding;
import com.greezxii.mobilecontroller.recycler.InspectionsRecyclerAdapter;
import com.greezxii.mobilecontroller.repository.DataRepository;
import com.greezxii.mobilecontroller.viewmodel.MainViewModel;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void openSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
    }

    public void saveToTFTP(MenuItem item) {
        vm.saveToTFTP();
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

        // Open info button
        button = findViewById(R.id.button_openInfo);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(intent);
        });
    }

    public void showBar(CharSequence msg) {
        Snackbar snackbar = Snackbar.make(this, findViewById(R.id.button_openInfo),
                msg, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}