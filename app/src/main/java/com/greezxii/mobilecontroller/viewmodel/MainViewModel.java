package com.greezxii.mobilecontroller.viewmodel;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.util.concurrent.FutureCallback;
import com.greezxii.mobilecontroller.activities.MainActivity;
import com.greezxii.mobilecontroller.model.Address;
import com.greezxii.mobilecontroller.model.Card;
import com.greezxii.mobilecontroller.repository.DataRepository;
import com.greezxii.mobilecontroller.spinner.AddressArrayAdapter;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

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
    public List<Address> mDistinctAddresses;
    private FlexibleAdapter<IFlexible> mRecyclerAdapter;
    private AddressArrayAdapter mSpinnerAdapter;
    private Address mAddressFilter;

    public MainViewModel(DataRepository repository, AlertDialog alertDialog) {
        this.mCards = new ArrayList<>();
        this.mLiveCards = new MutableLiveData<>();
        this.mSelectedCard = new MutableLiveData<>();
        this.mDistinctAddresses = new ArrayList<>();
        this.mRepository = repository;
        this.mAlertDialog = alertDialog;
        this.mAddressFilter = null;
        getCardsFromDB();
        getDistinctAddresses();
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
                String message = "Не удалось получить количество выполненных обходов:\n"
                        + t.getMessage();
                showMessageBox("Ошибка", message);
            }
        };
        mRepository.getPerformedInspectionsCount(callback);
    }

    public void setFilter(Address filter) {
        if (filter != mAddressFilter) {
            mAddressFilter = filter;
            getCardsFromDB();
        }
    }

    public void updateRecyclerView() {
        if (mRecyclerAdapter != null)
            mRecyclerAdapter.updateDataSet(MainActivity.createFlexibleList(mCards));
    }

    public void setRecyclerAdapter(FlexibleAdapter<IFlexible> adapter) {
        mRecyclerAdapter = adapter;
    }

    public void setSpinnerAdapter(AddressArrayAdapter adapter) {
        mSpinnerAdapter = adapter;
    }

    public void onSelect(int position) {
        updateSelectedCard();
        updatePerformedInspectionsCount();
        Card selected = mCards.get(position);
        mSelectedCard.setValue(selected);
    }

    public void getCardsFromDB() {
        //mRepository.populateWithTestData(callback_mock);
        FutureCallback<List<Card>> callback = new FutureCallback<List<Card>>() {
            @Override
            public void onSuccess(List<Card> result) {
                // Load from DB
                mCards = result;
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
        if (mAddressFilter == null)
            mRepository.getCards(callback);
        else
            mRepository.getCardsByBuildingAddress(mAddressFilter, callback);
    }

    public void getDistinctAddresses() {
        FutureCallback<List<Address>> callback = new FutureCallback<List<Address>>() {
            @Override
            public void onSuccess(List<Address> addresses) {
                // Populate spinner
                mSpinnerAdapter.clear();
                addresses.add(0, new Address());
                mSpinnerAdapter.addAll(addresses);
            }

            @Override
            public void onFailure(Throwable t) {
                String message = "Не удалось выполнить загрузку уникальных адресов " +
                        "из базы данных:\n" + t.getMessage();
                showMessageBox("Ошибка", message);
            }
        };
        mRepository.getDistinctAddresses(callback);
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
