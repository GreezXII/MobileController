package com.greezxii.mobilecontroller.viewmodel;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.util.concurrent.FutureCallback;
import com.greezxii.mobilecontroller.activities.MainActivity;
import com.greezxii.mobilecontroller.repository.DataRepository;
import com.greezxii.mobilecontroller.model.Inspection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

public class MainViewModel extends ViewModel {

    private final AlertDialog mAlertDialog;
    private final DataRepository mRepository;
    public MutableLiveData<List<Inspection>> mLiveInspections;
    public List<Inspection> mInspections;
    public MutableLiveData<Inspection> mSelectedInspection;
    public MutableLiveData<Integer> mInspectionsCount;
    public MutableLiveData<Integer> mPerformedInspectionsCount;
    public List<String> mDistinctAddresses;
    private FlexibleAdapter<IFlexible> mRecyclerAdapter;
    private ArrayAdapter<String> mSpinnerAdapter;

    public MainViewModel(DataRepository repository, AlertDialog alertDialog) {
        this.mInspections = new ArrayList<>();
        this.mLiveInspections = new MutableLiveData<>();
        this.mSelectedInspection = new MutableLiveData<>();
        this.mDistinctAddresses = new ArrayList<>();
        this.mRepository = repository;
        this.mAlertDialog = alertDialog;
        getInspectionsFromDB();
        this.mInspectionsCount = new MutableLiveData<>();
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
                String message = "Не удалось получить количество выполненных обходов. "
                        + t.getMessage();
                showMessageBox("Ошибка", message);
            }
        };
        mRepository.getPerformedInspectionsCount(callback);
    }

    public void updateRecyclerView() {
        if (mRecyclerAdapter != null)
            mRecyclerAdapter.updateDataSet(MainActivity.createFlexibleList(mInspections));
    }
    
    public void setRecyclerAdapter(FlexibleAdapter<IFlexible> adapter) {
        mRecyclerAdapter = adapter;
    }

    public void setSpinnerAdapter(ArrayAdapter<String> adapter) {
        mSpinnerAdapter = adapter;
    }

    public void onSelect(int position) {
        updateSelectedInspection();
        updatePerformedInspectionsCount();
        Inspection selected = mInspections.get(position);
        mSelectedInspection.setValue(selected);
    }

    public void getInspectionsFromDB() {
        //repository.makeInspectionsCacheFromMock();
        FutureCallback<List<Inspection>> callback = new FutureCallback<List<Inspection>>() {
            @Override
            public void onSuccess(List<Inspection> result) {
                mInspections = result;
                mInspectionsCount.setValue(mInspections.size());
                if (!mInspections.isEmpty()) {
                    mLiveInspections.setValue(mInspections);
                    mSelectedInspection.setValue(mInspections.get(0));
                }
                updateRecyclerView();
                SortedSet<String> distinctAddresses = new TreeSet<>();
                distinctAddresses.add("");
                for (Inspection i: mInspections) {
                    distinctAddresses.add(i.getAddress());
                }
                mSpinnerAdapter.clear();
                for (String address : distinctAddresses) {
                    mSpinnerAdapter.add(address);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                String message = "Не удалось выполнить загрузку из базы данных. " + t.getMessage();
                showMessageBox("Ошибка", message);
            }
        };
        mRepository.getAllInspections(callback);
    }

    public void updateSelectedInspection() {
        FutureCallback<Void> callback = new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                //showMessageBox("Информация", "Запись успешно обновлена.");
            }

            @Override
            public void onFailure(Throwable t) {
                String message = "Не удалось обновить запись в базе данных. " + t.getMessage();
                showMessageBox("Ошибка", message);
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
                String message = "Не удалось удалить запись в базе данных. " + t.getMessage();
                showMessageBox("Ошибка", message);
            }
        };
        mRepository.deleteInspections(mInspections, callback);
    }

    public void saveToTFTP() {
        FutureCallback<Void> callback = new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                showMessageBox("Уведомление", "Выгрузка данных выполнена успешно.");
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                String message = "Не удалось сохранить данные на TFTP сервер. " + t.getMessage();
                showMessageBox("Ошибка", message);
            }
        };
        getInspectionsFromDB();
        mRepository.saveInspectionsToTFTPAsync(mInspections, callback);
    }

    public void loadFromTFTP() {
        FutureCallback<List<Inspection>> callback = new FutureCallback<List<Inspection>>() {
            @Override
            public void onSuccess(List<Inspection> result) {
                showMessageBox("Уведомление", "Загрузка данных из TFTP выполнена успешно.");
                getInspectionsFromDB();
                updateRecyclerView();
            }

            @Override
            public void onFailure(Throwable t) {
                String message = "Не удалось загрузить данные из TFTP сервера. " + t.getMessage();
                showMessageBox("Ошибка", message);
            }
        };
        mRepository.loadInspectionsFromTFTPAsync(callback);
    }
}
