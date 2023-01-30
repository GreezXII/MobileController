package com.greezxii.mobilecontroller.repository;

import android.content.Context;

import com.greezxii.mobilecontroller.database.Inspection;
import com.greezxii.mobilecontroller.database.InspectionDao;
import com.greezxii.mobilecontroller.database.MobileControllerDatabase;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPClient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DataRepository {

    Context _context;
    public String TFTP_SERVER_IP = "192.168.43.93";
    public String INPUT_FILE_NAME = "input.db";
    public MobileControllerDatabase db;

    public DataRepository(Context context) {
        _context = context;
        db = MobileControllerDatabase.getDatabase(_context);
    }

    private String getFileContentFromTFTP() {
        class Worker extends Thread {
            String result = null;
            @Override
            public void run() {
                super.run();
                try {
                    TFTPClient client = new TFTPClient();
                    client.open();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    int bytesRead = client.receiveFile(INPUT_FILE_NAME, TFTP.BINARY_MODE, outputStream, TFTP_SERVER_IP);
                    if (bytesRead > 0) {
                        result = outputStream.toString("Cp1251");
                    }
                    client.close();
                } catch (Exception e) {
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
        return worker.result;
    }

    private ArrayList<Inspection> parseInspections(String fileContent) {
        String[] lines = fileContent.split("\r\n");

        ArrayList<Inspection> entities = new ArrayList<>();
        for (String l : lines) {
            if (l.length() < 1)
                continue;
            Inspection inspection = new Inspection();
            inspection.fromString(l);
            entities.add(inspection);
        }
        return entities;
    }

    public void makeInspectionsCacheFromTFTP() {
        class Worker extends Thread {
            @Override
            public void run() {
                super.run();
                String tftpFileContent = getFileContentFromTFTP();
                ArrayList<Inspection> inspections = parseInspections(tftpFileContent);
                InspectionDao inspectionDao = db.inspectionDao();
                inspectionDao.insertAll(inspections.toArray(new Inspection[0]));
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

    public void makeInspectionsCacheFromMock() {
        class Worker extends Thread {
            @Override
            public void run() {
                super.run();
                InspectionDao inspectionDao = db.inspectionDao();
                List<Inspection> inspections = inspectionDao.getAllInspections();
                if (!inspections.isEmpty())
                    return;
                try(InputStream inputStream = _context.getAssets().open("input.db")) {
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
