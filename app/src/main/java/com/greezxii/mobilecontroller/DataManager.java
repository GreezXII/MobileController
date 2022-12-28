package com.greezxii.mobilecontroller;

import com.greezxii.mobilecontroller.database.InspectionEntity;
import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPClient;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DataManager {
    public String TFTP_SERVER_IP = "192.168.43.93";  // 127.0.0.1
    public String INPUT_FILE_NAME = "input.db";

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

    public ArrayList<InspectionEntity> loadEntities() {
        String fileContent = getFileContentFromTFTP();
        String[] lines = fileContent.split("\r\n");

        ArrayList<InspectionEntity> entities = new ArrayList<InspectionEntity>();
        for (String l : lines) {
            if (l.length() < 1)
                continue;
            InspectionEntity inspection = new InspectionEntity();
            inspection.fromString(l);
            entities.add(inspection);
        }
        return entities;
    }
}
