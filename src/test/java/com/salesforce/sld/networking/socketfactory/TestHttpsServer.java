package com.salesforce.sld.networking.socketfactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import java.io.IOException;
import java.net.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestHttpsServer implements Runnable {
    protected ExecutorService workerExecutor = Executors.newFixedThreadPool(10);
    @Override
    public void run() {
            try {
                System.out.println("entering the server");
                SSLServerSocket serversocket = getServerSocket(new InetSocketAddress(InetAddress.getByName("localhost"), 8443));
                System.out.println("Server socket created");
                while(true) {
                    System.out.println("Waiting for client connection");
                    Socket s = serversocket.accept();
                    System.out.println("Server accepted the client connection");
                    this.workerExecutor.execute(new TestWorkerThread(s));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public SSLServerSocket getServerSocket(InetSocketAddress address){
        String keytoreCerts = "/certificates/cert-and-key.jks";
        String keystorePassword = "password";
        SSLServerSocket serverSocket = null;
        try {
            serverSocket = (SSLServerSocket) getSSLContext(keytoreCerts, keystorePassword).getServerSocketFactory().createServerSocket(address.getPort(), 0, address.getAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverSocket;
    }
    public SSLContext getSSLContext(String keystoreCerts, String keystorePassword){
        SSLContext ctx = null;
        try {
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(getClass().getResourceAsStream(keystoreCerts), keystorePassword.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, keystorePassword.toCharArray());
            ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(kmf.getKeyManagers(), null, null);
        }
        catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException | UnrecoverableKeyException | KeyManagementException e){
            e.printStackTrace();
        }
        return ctx;
    }
    public void stopExecutors()
    {
        workerExecutor.shutdownNow();
    }
}
