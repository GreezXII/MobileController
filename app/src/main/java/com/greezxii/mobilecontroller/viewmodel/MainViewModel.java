package com.greezxii.mobilecontroller.viewmodel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.greezxii.mobilecontroller.repository.DataRepository;
import com.greezxii.mobilecontroller.database.Inspection;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final DataRepository repository;
    public List<Inspection> inspections;
    public MutableLiveData<Inspection> selectedInspection;
    public MutableLiveData<Integer> performedInspectionsCount;

    public MainViewModel(DataRepository repository) {
        this.repository = repository;
        getInspections();
        performedInspectionsCount = new MutableLiveData<>();
        updatePerformedInspectionsCount();
    }

    private void updatePerformedInspectionsCount() {
        Integer value = repository.getPerformedInspectionsCount();
        performedInspectionsCount.setValue(value);
    }

    public List<Inspection> getInspections() {
        if (inspections == null) {
            repository.makeInspectionsCacheFromMock();
            inspections = repository.getAllInspections();
        }
        selectedInspection = new MutableLiveData<>(inspections.get(0));
        return inspections;
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

    public void updateSelectedInspection() {
        repository.updateInspection(selectedInspection.getValue());
    }

    public void saveToTFTP() {
        repository.putInspectionsToTFTPAsync();
    }
}
