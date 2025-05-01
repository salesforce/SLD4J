package com.salesforce.sld.networking.socketfactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class TestWorkerThread implements Runnable{
    protected Socket workersocket = null;
    public TestWorkerThread(Socket workersocket){
        this.workersocket = workersocket;
    }
    @Override
    public void run() {
        System.out.println("Talking to worker thread");
        try {
            OutputStream out = workersocket.getOutputStream();
            out.write(("WorkerThread Initiate Handshake: ").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
