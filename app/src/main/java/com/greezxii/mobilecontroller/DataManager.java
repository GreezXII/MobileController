package com.greezxii.mobilecontroller;

import android.content.Context;

import com.greezxii.mobilecontroller.database.Inspection;
import com.greezxii.mobilecontroller.database.InspectionDao;
import com.greezxii.mobilecontroller.database.MobileControllerDatabase;
import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPClient;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public String TFTP_SERVER_IP = "192.168.43.93";  // 127.0.0.1
    public String INPUT_FILE_NAME = "input.db";
    public MobileControllerDatabase db = null;
    public DataManager(Context context) {
        db = MobileControllerDatabase.getDatabase(context);
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
    private ArrayList<Inspection> parseEntities(String fileContent) {
        String[] lines = fileContent.split("\r\n");

        ArrayList<Inspection> entities = new ArrayList<Inspection>();
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
                ArrayList<Inspection> entities = parseEntities(tftpFileContent);
                InspectionDao inspectionDao = db.inspectionDao();
                inspectionDao.insertAll(entities.toArray(new Inspection[0]));
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
}
