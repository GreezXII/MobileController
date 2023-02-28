package com.greezxii.mobilecontroller.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.greezxii.mobilecontroller.R;
import com.greezxii.mobilecontroller.model.Inspection;
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

    private ActivityMainBinding mBinding;
    public MainViewModel mViewModel;
    private FlexibleAdapter<IFlexible> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        initButtons();
        initRecycler();
        initSpinner();
    }

    private void initViewModel() {
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
        DataRepository dataRepository = new DataRepository(this, executorService);
        mViewModel = new MainViewModel(dataRepository, createAlertDialog());
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setLifecycleOwner(this);
        mBinding.setVm(mViewModel);
    }

    private void initRecycler() {
        List<IFlexible> flexibleList = createFlexibleList(mViewModel.mInspections);
        mAdapter = new FlexibleAdapter<>(flexibleList);
        mAdapter.setMode(SelectableAdapter.Mode.SINGLE);
        if (!flexibleList.isEmpty())
            mAdapter.toggleSelection(0);
        RecyclerView recycler = findViewById(R.id.recycler_inspections);
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler.setAdapter(mAdapter);
        // Divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, RecyclerView.VERTICAL);
        Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.divider_drawable, null);
        if(divider != null)
            dividerItemDecoration.setDrawable(divider);
        recycler.addItemDecoration(dividerItemDecoration);
        // Click listener
        mAdapter.mItemClickListener = (view, position) -> {
            mAdapter.toggleSelection(position);
            mViewModel.onSelect(position);
            return true;
        };
        mViewModel.setRecyclerAdapter(mAdapter);
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

    private void initSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, mViewModel.mDistinctAddresses);
        spinnerAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        mBinding.spinnerAddresses.setAdapter(spinnerAdapter);
        mBinding.spinnerAddresses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAddress = (String)parent.getItemAtPosition(position);
                mViewModel.setFilter(selectedAddress);
                mViewModel.getInspectionsFromDB();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mViewModel.setFilter("");
            }
        });
        mViewModel.setSpinnerAdapter(spinnerAdapter);
    }

    private AlertDialog createAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false)
                .setNeutralButton("Закрыть", (dialog, which) -> dialog.cancel());
        return alertDialogBuilder.create();
    }

    public static List<IFlexible> createFlexibleList(List<Inspection> data) {
        List<IFlexible> flexibleList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++)
            flexibleList.add(new InspectionFlexibleItem(data.get(i)));
        return flexibleList;
    }

    @Override
    protected void onPause() {
        mViewModel.updateSelectedInspection();
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
        mViewModel.saveToTFTP();
    }

    public void loadFromTFTP(MenuItem item) {
        mViewModel.loadFromTFTP();
        List<IFlexible> flexibleList = createFlexibleList(mViewModel.mInspections);
        mAdapter.updateDataSet(flexibleList);
    }

    public void showBar(CharSequence msg) {
        Snackbar snackbar = Snackbar.make(this, findViewById(R.id.button_openInfo),
                msg, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}