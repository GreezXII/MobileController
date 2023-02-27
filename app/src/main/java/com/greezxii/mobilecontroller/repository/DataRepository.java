package com.greezxii.mobilecontroller.repository;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.greezxii.mobilecontroller.model.Inspection;
import com.greezxii.mobilecontroller.database.InspectionDao;
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

    private List<Inspection> parseInspections(String tftpFileContent) {
        // Parse Inspections
        String[] lines = tftpFileContent.split("\r\n");
        ArrayList<Inspection> inspections = new ArrayList<>();
        for (String l : lines) {
            if (l.length() < 1)
                continue;
            Inspection inspection = new Inspection();
            inspection.fromString(l);
            inspections.add(inspection);
        }
        return inspections;
    }

    public void saveInspectionsToTFTPAsync(List<Inspection> inspections, FutureCallback<Void> callback) {
        ListenableFuture<Void> future = mExecutorService.submit(() -> {
            // Create string content of file
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < inspections.size(); i++) {
                Inspection inspection = inspections.get(i);
                if (inspection.value != null) {
                    builder.append(inspection);
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

    public void loadInspectionsFromTFTPAsync(FutureCallback<List<Inspection>> callback) {
        ListenableFuture<List<Inspection>> future = mExecutorService.submit(() -> {
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

            List<Inspection> inspections = parseInspections(tftpFileContent);
            InspectionDao inspectionDao = db.inspectionDao();
            inspectionDao.deleteAll();
            inspectionDao.insertAll(inspections.toArray(new Inspection[0]));
            return inspections;
        });
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }

    public void makeInspectionsCacheFromMock(FutureCallback<Void> callback) {
        ListenableFuture<Void> future = mExecutorService.submit(() -> {
            InspectionDao inspectionDao = db.inspectionDao();
            List<Inspection> inspections = inspectionDao.getAllInspections();
            if (!inspections.isEmpty())
                return null;
            try(InputStream inputStream = mContext.getAssets().open("input.db")) {
                String fileContent = IOUtils.toString(inputStream, Charset.defaultCharset());
                inspections = parseInspections(fileContent);
                inspectionDao.insertAll(inspections.toArray(new Inspection[0]));
            } catch (IOException e) {
                throw new Exception("Не удалось загрузить файл из TFTP сервера.");
            }
            return null;
        });
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }

    public void getAllInspections(FutureCallback<List<Inspection>> callback) {
        ListenableFuture<List<Inspection>> future = mExecutorService.submit(() -> {
            InspectionDao inspectionDao = db.inspectionDao();
            return inspectionDao.getAllInspections();
        });
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }

    public void updateInspection(Inspection inspection, FutureCallback<Void> callback) {
        ListenableFuture<Void> future = mExecutorService.submit(() -> {
            InspectionDao inspectionDao = db.inspectionDao();
            inspectionDao.updateInspection(inspection);
            return null;
        });
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }

    public void deleteInspections(List<Inspection> inspections, FutureCallback<Void> callback) {
        ListenableFuture<Void> future = mExecutorService.submit(() -> {
            InspectionDao inspectionDao = db.inspectionDao();
            inspectionDao.deleteAll(inspections.toArray(new Inspection[0]));
            return null;
        });
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }

    public void getPerformedInspectionsCount(FutureCallback<Integer> callback) {
        ListenableFuture<Integer> future = mExecutorService.submit(() -> {
            return db.inspectionDao().getPerformedInspectionsCount();
        });
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(mContext));
    }
}
