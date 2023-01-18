package com.greezxii.mobilecontroller.ViewModel;

import androidx.lifecycle.ViewModel;

import com.greezxii.mobilecontroller.Repository.DataRepository;
import com.greezxii.mobilecontroller.database.Inspection;
import java.util.List;

public class MainViewModel extends ViewModel {
    final DataRepository repository;
    List<Inspection> _inspections;

    public MainViewModel(DataRepository repository)
    {
        this.repository = repository;
    }

    public List<Inspection> getInspections() {
        if (_inspections == null) {
            repository.makeInspectionsCacheFromMock();
            _inspections = repository.getAllInspections();
        }
        return _inspections;
    }
}
