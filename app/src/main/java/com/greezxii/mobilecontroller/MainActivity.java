package com.greezxii.mobilecontroller;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.greezxii.mobilecontroller.database.Inspection;
import com.greezxii.mobilecontroller.databinding.ActivityMainBinding;
import com.greezxii.mobilecontroller.recycler.InspectionFlexibleItem;
import com.greezxii.mobilecontroller.repository.DataRepository;
import com.greezxii.mobilecontroller.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        initButtons();
        initRecycler(viewModel.inspections);
    }

    private void initViewModel() {
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
        DataRepository dataRepository = new DataRepository(this, executorService);
        viewModel = new MainViewModel(dataRepository, createAlertDialog());

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);
    }

    private void initRecycler(List<Inspection> data) {
        List<IFlexible> flexibleList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++)
            flexibleList.add(new InspectionFlexibleItem(data.get(i)));

        FlexibleAdapter<IFlexible> adapter = new FlexibleAdapter<>(flexibleList);
        adapter.setMode(SelectableAdapter.Mode.SINGLE);
        RecyclerView recycler = findViewById(R.id.recycler_inspections);
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler.setAdapter(adapter);
        // Divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, RecyclerView.VERTICAL);
        Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.divider_drawable, null);
        if(divider != null)
            dividerItemDecoration.setDrawable(divider);
        recycler.addItemDecoration(dividerItemDecoration);
        adapter.mItemClickListener = new FlexibleAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position) {
                adapter.toggleSelection(position);
                viewModel.onSelect(position);
                return true;
            }
        };
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

    private AlertDialog createAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false)
                .setNeutralButton("Закрыть", (dialog, which) -> dialog.cancel());
        return alertDialogBuilder.create();
    }

    @Override
    protected void onPause() {
        viewModel.updateSelectedInspection();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void openSettings(MenuItem Item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
    }

    public void saveToTFTP(MenuItem item) {
        viewModel.saveToTFTP();
    }

    public void loadFromTFTP(MenuItem item) {
        viewModel.loadFromTFTP();
    }

    public void showBar(CharSequence msg) {
        Snackbar snackbar = Snackbar.make(this, findViewById(R.id.button_openInfo),
                msg, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}