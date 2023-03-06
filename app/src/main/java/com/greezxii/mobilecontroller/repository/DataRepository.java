package com.greezxii.mobilecontroller.repository;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.greezxii.mobilecontroller.database.CardDao;
import com.greezxii.mobilecontroller.model.Address;
import com.greezxii.mobilecontroller.model.Card;
import com.greezxii.mobilecontroller.database.MobileControllerDatabase;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataRepository {

    ListeningExecutorService mExecutorService;
    Context mContext;
    public String TFTP_SERVER_IP = "192.168.1.158";
    public String INPUT_FILE_NAME = "input.db";
    public String NOTE_FILE_NAME = "obxod.txt";
    public MobileControllerDatabase db;

    public DataRepository(Context context, ListeningExecutorService executorService) {
        this.mContext = context;
        this.mExecutorService = executorService;
        db = MobileControllerDatabase.getDatabase(mContext);
    }

    private List<Card> parseCards(String inputFileContent, String noteFileContexnt) {
        // Parse cards
        String[] lines = inputFileContent.split("\r\n");
        ArrayList<Card> cards = new ArrayList<>();
        for (String l : lines) {
            if (l.length() < 1)
                continue;
            Card card = new Card();
            card.fromString(l, noteFileContexnt);
            cards.add(card);
        }
        return cards;
    }

    public void saveCardsToTFTPAsync(List<Card> cards, FutureCallback<Void> callback) {
        ListenableFuture<Void> future = mExecutorService.submit(() -> {
            // Create string content of file
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < cards.size(); i++) {
                Card card = cards.get(i);
                if (card.consumption != null) {
                    builder.append(card);
                }
            }
            // Create byte stream
            byte[] inputStreamContent = builder.toString().getBytes("Cp1251");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(inputStreamContent);
            // Create filename
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd", new Locale("ru"));
            String fileName = formatter.format(currentDate) + ".db";
            // Send data to TFTP
            TFTPClient client = new TFTPClient();
            client.open();
            client.sendFile(fileName, TFTP.BINARY_MODE, inputStream, TFTP_SERVER_IP);
            client.close();
            return null;
        });
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }

    public void loadCardsFromTFTPAsync(FutureCallback<List<Card>> callback) {
        ListenableFuture<List<Card>> future = mExecutorService.submit(() -> {
            // Read file content from TFTP
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TFTPClient client = new TFTPClient();
            client.open();
            //input.db
            int bytesRead = client.receiveFile(INPUT_FILE_NAME, TFTP.BINARY_MODE, outputStream, TFTP_SERVER_IP);
            String inputFileContent;
            if (bytesRead > 0) {
                inputFileContent = outputStream.toString("Cp1251");
                outputStream.reset();
            }
            else {
                throw new Exception("Не удалось загрузить файл " + INPUT_FILE_NAME + " из TFTP сервера.");
            }
            //obxod.txt
            bytesRead = client.receiveFile(NOTE_FILE_NAME, TFTP.BINARY_MODE, outputStream, TFTP_SERVER_IP);
            String noteFileContent;
            if (bytesRead > 0) {
                noteFileContent = outputStream.toString("Cp1251");
                outputStream.reset();
            }
            else {
                throw new Exception("Не удалось загрузить файл" + NOTE_FILE_NAME + " из TFTP сервера.");
            }
            client.close();

            List<Card> cards = parseCards(inputFileContent, noteFileContent);
            CardDao cardDao = db.cardsDao();
            cardDao.deleteAll();
            cardDao.insertAll(cards.toArray(new Card[0]));
            return cards;
        });
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }

    public void getCards(FutureCallback<List<Card>> callback) {
        ListenableFuture<List<Card>> future = mExecutorService.submit(() -> {
            CardDao cardDao = db.cardsDao();
            return cardDao.getAllCards();
        });
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }

    public void getCardsByBuildingAddress(Address address, FutureCallback<List<Card>> callback) {
        ListenableFuture<List<Card>> future = mExecutorService.submit(() -> {
            CardDao cardDao = db.cardsDao();
            return cardDao.getCardsByBuildingAddress(address.street, address.buildingNumber,
                        address.buildingLetter, address.blockNumber, address.blockLetter);
        });
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }

    public void getDistinctAddresses(FutureCallback<List<Address>> callback) {
        ListenableFuture<List<Address>> future = mExecutorService.submit(() -> {
            CardDao cardDao = db.cardsDao();
            return cardDao.getDistinctBuildingAddresses();
        });
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }

    public void updateCard(Card card, FutureCallback<Void> callback) {
        ListenableFuture<Void> future = mExecutorService.submit(() -> {
            CardDao cardDao = db.cardsDao();
            cardDao.updateCard(card);
            return null;
        });
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }

    public void deleteCard(List<Card> cards, FutureCallback<Void> callback) {
        ListenableFuture<Void> future = mExecutorService.submit(() -> {
            CardDao cardDao = db.cardsDao();
            cardDao.deleteAll(cards.toArray(new Card[0]));
            return null;
        });
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }

    public void getPerformedInspectionsCount(FutureCallback<Integer> callback) {
        ListenableFuture<Integer> future = mExecutorService.submit(() ->
                db.cardsDao().getPerformedInspectionsCount());
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }
}
