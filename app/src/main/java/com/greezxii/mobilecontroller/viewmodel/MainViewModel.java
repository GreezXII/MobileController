package com.greezxii.mobilecontroller.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.greezxii.mobilecontroller.repository.DataRepository;
import com.greezxii.mobilecontroller.database.Inspection;
import java.util.List;

public class MainViewModel extends ViewModel {
    private final DataRepository repository;

    public MainViewModel(DataRepository repository)
    {
        this.repository = repository;
    }

    public List<Inspection> inspections;
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

    public MutableLiveData<Inspection> selectedInspection;

    public void onSelect(int position) {
        repository.updateInspection(selectedInspection.getValue());
        Inspection selected = inspections.get(position);
        selectedInspection.setValue(selected);
    };

}
