package com.greezxii.mobilecontroller.viewmodel;

import androidx.lifecycle.ViewModel;

import com.greezxii.mobilecontroller.repository.DataRepository;
import com.greezxii.mobilecontroller.database.Inspection;
import java.util.List;

public class MainViewModel extends ViewModel {
    final DataRepository repository;
    List<Inspection> _inspections;
    Inspection selectedInspection;


    public MainViewModel(DataRepository repository)
    {
        this.repository = repository;
    }

    public List<Inspection> getInspections() {
        if (_inspections == null) {
            repository.makeInspectionsCacheFromMock();
            _inspections = repository.getAllInspections();
        }
        selectedInspection = _inspections.get(0);
        return _inspections;
    }

    public Inspection getSelectedInspection() {
        return selectedInspection;
    }

    public void setSelectedInspection(Inspection selectedInspection) {
        this.selectedInspection = selectedInspection;
    }
}
