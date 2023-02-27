package com.greezxii.mobilecontroller.viewmodel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.util.concurrent.FutureCallback;
import com.greezxii.mobilecontroller.activities.MainActivity;
import com.greezxii.mobilecontroller.repository.DataRepository;
import com.greezxii.mobilecontroller.database.Inspection;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

public class MainViewModel extends ViewModel {

    private final AlertDialog mAlertDialog;
    private final DataRepository mRepository;
    public MutableLiveData<List<Inspection>> mLiveInspections;
    public List<Inspection> mInspections;
    public MutableLiveData<Inspection> mSelectedInspection;
    public MutableLiveData<Integer> mPerformedInspectionsCount;
    private FlexibleAdapter<IFlexible> mAdapter;

    public MainViewModel(DataRepository repository, AlertDialog alertDialog) {
        this.mInspections = new ArrayList<>();
        this.mLiveInspections = new MutableLiveData<>();
        this.mSelectedInspection = new MutableLiveData<>();
        this.mRepository = repository;
        this.mAlertDialog = alertDialog;
        loadInspectionsFromDB();
        this.mPerformedInspectionsCount = new MutableLiveData<>();
        updatePerformedInspectionsCount();
    }

    private void showMessageBox(String title, String message) {
        mAlertDialog.setTitle(title);
        mAlertDialog.setMessage(message);
        mAlertDialog.show();
    }

    private void updatePerformedInspectionsCount() {
        FutureCallback<Integer> callback = new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                mPerformedInspectionsCount.setValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                showMessageBox("Ошибка", "Не удалось получить количество выполненных обходов");
            }
        };
        mRepository.getPerformedInspectionsCount(callback);
    }

    public void updateRecyclerView() {
        if (mAdapter != null)
            mAdapter.updateDataSet(MainActivity.createFlexibleList(mInspections));
    }

    public void setAdapter(FlexibleAdapter<IFlexible> adapter) {
        mAdapter = adapter;
    }

    public void loadInspectionsFromDB() {
        //repository.makeInspectionsCacheFromMock();
        FutureCallback<List<Inspection>> callback = new FutureCallback<List<Inspection>>() {
            @Override
            public void onSuccess(List<Inspection> result) {
                mInspections = result;
                if (!mInspections.isEmpty()) {
                    mLiveInspections.setValue(mInspections);
                    mSelectedInspection.setValue(mInspections.get(0));
                }
                updateRecyclerView();
            }

            @Override
            public void onFailure(Throwable t) {
                showMessageBox("Ошибка", "Не удалось выполнить загрузку из базы данных.");
            }
        };
        mRepository.getAllInspections(callback);
    }

    public void updateSelectedInspection() {
        FutureCallback<Void> callback = new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                showMessageBox("Информация", "Запись успешно обновлена.");
            }

            @Override
            public void onFailure(Throwable t) {
                showMessageBox("Ошибка", "Не удалось обновить запись в базе данных.");
            }
        };
        mRepository.updateInspection(mSelectedInspection.getValue(), callback);
    }

    public void deleteInspections() {
        FutureCallback<Void> callback = new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                showMessageBox("Информация", "Запись успешно удалена.");
            }

            @Override
            public void onFailure(Throwable t) {
                showMessageBox("Ошибка", "Не удалось удалить запись в базе данных.");
            }
        };
        mRepository.deleteInspections(mInspections, callback);
    }

    public void onSelect(int position) {
        updateSelectedInspection();
        updatePerformedInspectionsCount();
        Inspection selected = mInspections.get(position);
        mSelectedInspection.setValue(selected);
    }

    public void saveToTFTP() {
        FutureCallback<Void> callback = new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                showMessageBox("Уведомление", "Выгрузка данных выполнена успешно.");
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                showMessageBox("Произошла ошибка", t.getMessage());
            }
        };
        loadInspectionsFromDB();
        mRepository.saveInspectionsToTFTPAsync(mInspections, callback);
    }

    public void loadFromTFTP() {
        FutureCallback<List<Inspection>> callback = new FutureCallback<List<Inspection>>() {
            @Override
            public void onSuccess(List<Inspection> result) {
                showMessageBox("Уведомление", "Загрузка данных из TFTP выполнена успешно.");
                loadInspectionsFromDB();
                updateRecyclerView();
            }

            @Override
            public void onFailure(Throwable t) {
                showMessageBox("Произошла ошибка", t.getMessage());
            }
        };
        mRepository.loadInspectionsFromTFTPAsync(callback);
    }
}
