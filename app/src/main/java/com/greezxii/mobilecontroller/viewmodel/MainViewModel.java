package com.greezxii.mobilecontroller.viewmodel;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.util.concurrent.FutureCallback;
import com.greezxii.mobilecontroller.activities.MainActivity;
import com.greezxii.mobilecontroller.model.Card;
import com.greezxii.mobilecontroller.repository.DataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

public class MainViewModel extends ViewModel {

    private final AlertDialog mAlertDialog;
    private final DataRepository mRepository;
    public MutableLiveData<List<Card>> mLiveCards;
    public List<Card> mCards;
    public MutableLiveData<Card> mSelectedCard;
    public MutableLiveData<Integer> mInspectionsCount;
    public MutableLiveData<Integer> mPerformedInspectionsCount;
    public List<String> mDistinctAddresses;
    public String mFilter;
    private FlexibleAdapter<IFlexible> mRecyclerAdapter;
    private ArrayAdapter<String> mSpinnerAdapter;

    public MainViewModel(DataRepository repository, AlertDialog alertDialog) {
        this.mCards = new ArrayList<>();
        this.mLiveCards = new MutableLiveData<>();
        this.mSelectedCard = new MutableLiveData<>();
        this.mDistinctAddresses = new ArrayList<>();
        this.mRepository = repository;
        this.mAlertDialog = alertDialog;
        getCardsFromDB();
        this.mInspectionsCount = new MutableLiveData<>();
        this.mPerformedInspectionsCount = new MutableLiveData<>();
        updatePerformedInspectionsCount();
        mFilter = null;
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
                String message = "Не удалось получить количество выполненных обходов:\n"
                        + t.getMessage();
                showMessageBox("Ошибка", message);
            }
        };
        mRepository.getPerformedInspectionsCount(callback);
    }

    public void updateRecyclerView() {
        if (mRecyclerAdapter != null)
            mRecyclerAdapter.updateDataSet(MainActivity.createFlexibleList(mCards));
    }

    public void setRecyclerAdapter(FlexibleAdapter<IFlexible> adapter) {
        mRecyclerAdapter = adapter;
    }

    public void setSpinnerAdapter(ArrayAdapter<String> adapter) {
        mSpinnerAdapter = adapter;
    }

    public void onSelect(int position) {
        updateSelectedCard();
        updatePerformedInspectionsCount();
        Card selected = mCards.get(position);
        mSelectedCard.setValue(selected);
    }

    public void getCardsFromDB() {
        //mRepository.populateWithTestData();
        FutureCallback<List<Card>> callback = new FutureCallback<List<Card>>() {
            @Override
            public void onSuccess(List<Card> result) {
                // Load from DB
                mCards = result;

                // Populate spinner
                if (mFilter == null) {
                    SortedSet<String> distinctAddresses = new TreeSet<>();
                    distinctAddresses.add("");
                    for (Card c: mCards) {
                        distinctAddresses.add(c.address.getBuildingAddress());
                    }
                    mSpinnerAdapter.clear();
                    for (String address : distinctAddresses) {
                        mSpinnerAdapter.add(address);
                    }
                } else
                    // Apply filter
                    mCards.removeIf(insp -> !mFilter.equals(insp.address.getBuildingAddress()));

                // Update recycler view
                if (!mCards.isEmpty()) {
                    mLiveCards.setValue(mCards);
                    mSelectedCard.setValue(mCards.get(0));
                    mRecyclerAdapter.toggleSelection(0);
                }
                updateRecyclerView();
                mInspectionsCount.setValue(mCards.size());
            }

            @Override
            public void onFailure(Throwable t) {
                String message = "Не удалось выполнить загрузку из базы данных:\n" + t.getMessage();
                showMessageBox("Ошибка", message);
            }
        };
        mRepository.getCards(callback);
    }

    public void updateSelectedCard() {
        FutureCallback<Void> callback = new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                //showMessageBox("Информация", "Запись успешно обновлена.");
            }

            @Override
            public void onFailure(Throwable t) {
                String message = "Не удалось обновить запись в базе данных:\n" + t.getMessage();
                showMessageBox("Ошибка", message);
            }
        };
        mRepository.updateCard(mSelectedCard.getValue(), callback);
    }

    public void deleteCard() {
        FutureCallback<Void> callback = new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                showMessageBox("Информация", "Запись успешно удалена.");
            }

            @Override
            public void onFailure(Throwable t) {
                String message = "Не удалось удалить запись в базе данных:\n" + t.getMessage();
                showMessageBox("Ошибка", message);
            }
        };
        mRepository.deleteCard(mCards, callback);
    }

    public void saveToTFTP() {
        FutureCallback<Void> callback = new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                showMessageBox("Уведомление", "Выгрузка данных выполнена успешно.");
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                String message = "Не удалось сохранить данные на TFTP сервер:\n" + t.getMessage();
                showMessageBox("Ошибка", message);
            }
        };
        getCardsFromDB();
        mRepository.saveCardsToTFTPAsync(mCards, callback);
    }

    public void loadFromTFTP() {
        FutureCallback<List<Card>> callback = new FutureCallback<List<Card>>() {
            @Override
            public void onSuccess(List<Card> result) {
                showMessageBox("Уведомление", "Загрузка данных из TFTP выполнена успешно.");
                getCardsFromDB();
                updateRecyclerView();
            }

            @Override
            public void onFailure(Throwable t) {
                String message = "Не удалось загрузить данные из TFTP сервера:\n" + t.getMessage();
                showMessageBox("Ошибка", message);
            }
        };
        mRepository.loadCardsFromTFTPAsync(callback);
    }
}
