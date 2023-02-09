package com.greezxii.mobilecontroller.repository;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.greezxii.mobilecontroller.database.Inspection;
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

    ListeningExecutorService executorService;
    Context context;
    public String TFTP_SERVER_IP = "192.168.1.158";
    public String INPUT_FILE_NAME = "input.db";
    public MobileControllerDatabase db;

    public DataRepository(Context context, ListeningExecutorService executorService) {
        this.context = context;
        this.executorService = executorService;
        db = MobileControllerDatabase.getDatabase(this.context);
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

    public void saveInspectionsToTFTPAsync(FutureCallback<Void> callback) {
        ListenableFuture<Void> future = executorService.submit(() -> {
            // Create string content of file
            List<Inspection> inspections = getAllInspections();
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
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(context));
    }

    public void loadInspectionsFromTFTPAsync(FutureCallback<List<Inspection>> callback) {
        ListenableFuture<List<Inspection>> future = executorService.submit(() -> {
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
        Futures.addCallback(future, callback, ContextCompat.getMainExecutor(context));
    }

    public void makeInspectionsCacheFromMock() {
        class Worker extends Thread {
            @Override
            public void run() {
                super.run();
                InspectionDao inspectionDao = db.inspectionDao();
                List<Inspection> inspections = inspectionDao.getAllInspections();
                if (!inspections.isEmpty())
                    return;
                try(InputStream inputStream = context.getAssets().open("input.db")) {
                    String fileContent = IOUtils.toString(inputStream, Charset.defaultCharset());
                    inspections = parseInspections(fileContent);
                    inspectionDao.insertAll(inspections.toArray(new Inspection[0]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Worker worker = new Worker();
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Inspection> getAllInspections() {
        class Worker extends Thread {
            List<Inspection> result;
            @Override
            public void run() {
                super.run();
                InspectionDao inspectionDao = db.inspectionDao();
                result = inspectionDao.getAllInspections();
            }
        }
        Worker worker = new Worker();
        worker.start();
        try {
            worker.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return worker.result;
    }



    public void updateInspection(Inspection inspection) {
        class Worker extends Thread {
            @Override
            public void run() {
                super.run();
                InspectionDao inspectionDao = db.inspectionDao();
                inspectionDao.updateInspection(inspection);
            }
        }
        Worker worker = new Worker();
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteInspections(List<Inspection> inspections) {
        class Worker extends Thread {
            @Override
            public void run() {
                super.run();
                InspectionDao inspectionDao = db.inspectionDao();
                inspectionDao.deleteAll(inspections.toArray(new Inspection[0]));
            }
        }
        Worker worker = new Worker();
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Integer getPerformedInspectionsCount() {
        class Worker extends Thread {
            Integer result;
            @Override
            public void run() {
                super.run();
                result = db.inspectionDao().getPerformedInspectionsCount();
            }
        }
        Worker worker = new Worker();
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return worker.result;
    }
}
