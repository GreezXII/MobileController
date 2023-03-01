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
    public MobileControllerDatabase db;

    public DataRepository(Context context, ListeningExecutorService executorService) {
        this.mContext = context;
        this.mExecutorService = executorService;
        db = MobileControllerDatabase.getDatabase(mContext);
    }

    private List<Card> parseCards(String tftpFileContent) {
        // Parse cards
        String[] lines = tftpFileContent.split("\r\n");
        ArrayList<Card> cards = new ArrayList<>();
        for (String l : lines) {
            if (l.length() < 1)
                continue;
            Card card = new Card();
            card.fromString(l);
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
            int bytesRead = client.receiveFile(INPUT_FILE_NAME, TFTP.BINARY_MODE, outputStream, TFTP_SERVER_IP);
            String tftpFileContent;
            if (bytesRead > 0) {
                tftpFileContent = outputStream.toString("Cp1251");
            }
            else {
                throw new Exception("Не удалось загрузить файл из TFTP сервера.");
            }
            client.close();

            List<Card> cards = parseCards(tftpFileContent);
            CardDao cardDao = db.cardsDao();
            cardDao.deleteAll();
            cardDao.insertAll(cards.toArray(new Card[0]));
            return cards;
        });
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }

    public void populateWithTestData(FutureCallback<Void> callback) {
        ListenableFuture<Void> future = mExecutorService.submit(() -> {
            CardDao cardDao = db.cardsDao();
            List<Card> cards = cardDao.getAllCards();
            if (!cards.isEmpty())
                return null;
            try(InputStream inputStream = mContext.getAssets().open("input.db")) {
                String fileContent = IOUtils.toString(inputStream, Charset.defaultCharset());
                cards = parseCards(fileContent);
                cardDao.insertAll(cards.toArray(new Card[0]));
            } catch (IOException e) {
                throw new Exception("Не удалось загрузить файл из TFTP сервера.");
            }
            return null;
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
