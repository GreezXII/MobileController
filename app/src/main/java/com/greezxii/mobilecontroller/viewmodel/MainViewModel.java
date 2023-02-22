package com.greezxii.mobilecontroller.viewmodel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.util.concurrent.FutureCallback;
import com.greezxii.mobilecontroller.repository.DataRepository;
import com.greezxii.mobilecontroller.database.Inspection;

import java.util.List;

public class MainViewModel extends ViewModel {

    private final AlertDialog alertDialog;
    private final DataRepository repository;
    public MutableLiveData<List<Inspection>> liveInspections;
    public List<Inspection> inspections;
    public MutableLiveData<Inspection> selectedInspection;
    public MutableLiveData<Integer> performedInspectionsCount;

    public MainViewModel(DataRepository repository, AlertDialog alertDialog) {
        this.liveInspections = new MutableLiveData<>();
        selectedInspection = new MutableLiveData<>();
        this.repository = repository;
        this.alertDialog = alertDialog;
        loadInspectionsFromDB();
        performedInspectionsCount = new MutableLiveData<>();
        updatePerformedInspectionsCount();
    }

    private void showMessageBox(String title, String message) {
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    private void updatePerformedInspectionsCount() {
        Integer value = repository.getPerformedInspectionsCount();
        performedInspectionsCount.setValue(value);
    }

    public void loadInspectionsFromDB() {
        repository.makeInspectionsCacheFromMock();
        inspections = repository.getAllInspections();
        if (!inspections.isEmpty()) {
            liveInspections.setValue(inspections);
            selectedInspection.setValue(inspections.get(0));
        }
    }

    public void updateSelectedInspection() {
        repository.updateInspection(selectedInspection.getValue());
    }

    public void deleteInspections() {
        repository.deleteInspections(inspections);
    }

    public void onSelect(int position) {
        updateSelectedInspection();
        updatePerformedInspectionsCount();
        Inspection selected = inspections.get(position);
        selectedInspection.setValue(selected);
    }

    public void saveToTFTP() {
        FutureCallback<Void> callback = new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                showMessageBox("Уведомление", "Загрузка данных выполнена успешно.");
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                showMessageBox("Произошла ошибка", t.getMessage());
            }
        };
        repository.saveInspectionsToTFTPAsync(callback);
    }

    public void loadFromTFTP() {
        FutureCallback<List<Inspection>> callback = new FutureCallback<List<Inspection>>() {
            @Override
            public void onSuccess(List<Inspection> result) {
                loadInspectionsFromDB();
                showMessageBox("Уведомление", "Загрузка данных выполнена успешно.");
            }

            @Override
            public void onFailure(Throwable t) {
                showMessageBox("Произошла ошибка", t.getMessage());
            }
        };
        repository.loadInspectionsFromTFTPAsync(callback);
    }
}
