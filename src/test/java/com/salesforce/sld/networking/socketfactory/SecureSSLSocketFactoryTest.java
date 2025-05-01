package com.salesforce.sld.networking.socketfactory;

import org.junit.jupiter.api.*;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.*;

class SecureSSLSocketFactoryTest {
    public SecureSSLSocketFactory factory;
    static ExecutorService executorService;
    static TestHttpsServer server;
    static Map<String, Object> obj = null;

    @BeforeAll
    static void beforeAllInit() {
        Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
        try {
            InputStream conf = SecureSSLSocketFactoryTest.class.getClassLoader().getResourceAsStream("config.yaml");
            obj = yaml.load(conf);
            //Starting the TestHttpsServer
            executorService = Executors.newSingleThreadExecutor();
            server = new TestHttpsServer();
            executorService.execute(server);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void init() {
        factory = new SecureSSLSocketFactory();
    }

    @AfterAll
    static void cleanup() {
        server.stopExecutors();
        executorService.shutdownNow();
    }

    @Test
    void testDefaultSocketFactory() {
        try {
            assertNotNull(factory, "The socket object returned was null");
            SecureSSLSocket secureSocket = (SecureSSLSocket) factory.createSocket("localhost", 8443);
            System.out.println("Socket unique id: " + secureSocket.getSession().getId());
            assertTrue(secureSocket.getSession().isValid());
        } catch (IOException | UnsupportedOperationException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testTruststoreSocketFactory() {
        String trustedCerts = "certificates/cacert.jks";
        String truststorePass = "password";
        KeyStore truststore;
        try {
            truststore = KeyStore.getInstance("JKS");
            truststore.load(getClass().getClassLoader().getResourceAsStream(trustedCerts), truststorePass.toCharArray());
            SecureSSLSocketFactory factory = new SecureSSLSocketFactory(null, "", truststore);
            assertNotNull(factory, "The socket object returned was null");
            SecureSSLSocket secureSocket = (SecureSSLSocket) factory.createSocket("localhost", 8443);
            System.out.println("Socket unique id: " + secureSocket.getSession().getId());
            assertTrue(secureSocket.getSession().isValid());

        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testKeystoreSocketFactory() {
        String keystoreCerts = "certificates/cert-and-key.jks";
        KeyStore keystore;
        String keystorePass = "password";
        try {
            keystore = KeyStore.getInstance("JKS");
            keystore.load(getClass().getClassLoader().getResourceAsStream(keystoreCerts), keystorePass.toCharArray());
            SecureSSLSocketFactory factory = new SecureSSLSocketFactory(keystore, keystorePass, null);
            assertNotNull(factory, "The socket object returned was null");
            SecureSSLSocket secureSocket = (SecureSSLSocket) factory.createSocket("localhost", 8443);
            System.out.println("Socket unique id: " + secureSocket.getSession().getId());
//            assertTrue(secureSocket.getSession().isValid());

        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testTruststoreAndKeystoreSocketFactory() {
        String trustedCerts = "certificates/cacert.jks";
        String truststorePass = "password";
        KeyStore truststore;
        String keystoreCerts = "certificates/cert-and-key.jks";
        KeyStore keystore;
        String keystorePass = "password";
        try {
            truststore = KeyStore.getInstance("JKS");
            truststore.load(getClass().getClassLoader().getResourceAsStream(trustedCerts), truststorePass.toCharArray());
            keystore = KeyStore.getInstance("JKS");
            keystore.load(getClass().getClassLoader().getResourceAsStream(keystoreCerts), keystorePass.toCharArray());
            SecureSSLSocketFactory factory = new SecureSSLSocketFactory(keystore, keystorePass, truststore);
            assertNotNull(factory, "The socket object returned was null");
            SecureSSLSocket secureSocket = (SecureSSLSocket) factory.createSocket("localhost", 8443);
            System.out.println("Socket unique id: " + secureSocket.getSession().getId());
            assertTrue(secureSocket.getSession().isValid());

        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testNullTruststoreAndKeystoreFactory()  {
        try{
            assertThrows(IllegalArgumentException.class, () -> {
                new SecureSSLSocketFactory(null, "", null);
            });
        }
        catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testSetCipherSuites() {
        try {
            SecureSSLSocket secureSocket = (SecureSSLSocket) factory.createSocket("localhost", 8443);
            System.out.println("Socket unique id: " + secureSocket.getSession().getId());
            assertThrows(UnsupportedOperationException.class, () -> {
                secureSocket.setEnabledCipherSuites(new String[]{"ABC", "ABZ"});
            });
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testSetSSLParams() {
        try {
            SecureSSLSocket secureSocket = (SecureSSLSocket) factory.createSocket("localhost", 8443);
            SSLParameters params = new SSLParameters();
            params.setEndpointIdentificationAlgorithm("HTTPS");
            System.out.println("Socket unique id: " + secureSocket.getSession().getId());
            assertThrows(UnsupportedOperationException.class, () -> {
                secureSocket.setSSLParameters(params);
            });
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testSetEnabledProtocols() {
        try {
            SecureSSLSocket secureSocket = (SecureSSLSocket) factory.createSocket("localhost", 8443);
            System.out.println("Socket unique id: " + secureSocket.getSession().getId());
            assertThrows(UnsupportedOperationException.class, () -> {
                secureSocket.setEnabledProtocols(new String[]{"TLSv1.2"});
            });
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testGetEnabledProtocols() {
        try {
            SecureSSLSocket secureSocket = (SecureSSLSocket) factory.createSocket("localhost", 8443);
            System.out.println("Socket unique id: " + secureSocket.getSession().getId());
            ArrayList<String> al1 = (ArrayList<String>) obj.get("EnabledProtocols");
            String[] protocols = al1.toArray(new String[al1.size()]);
            assertArrayEquals(protocols, secureSocket.getEnabledProtocols());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testGetEnabledCipherSuites() {
        try {
            SecureSSLSocket secureSocket = (SecureSSLSocket) factory.createSocket("localhost", 8443);
            System.out.println("Socket unique id: " + secureSocket.getSession().getId());
            ArrayList<String> al2 = (ArrayList<String>) obj.get("CipherSuites");
            String[] ciphersuites = al2.toArray(new String[al2.size()]);
            assertArrayEquals(ciphersuites, secureSocket.getEnabledCipherSuites());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    void testCreateSocketWithInetAddress() {
        try {
            SecureSSLSocket secureSocket = (SecureSSLSocket) factory.createSocket(InetAddress.getByName("localhost"), 8443);
            System.out.println("Socket unique id: " + secureSocket.getSession().getId());
//            assertTrue(secureSocket.getSession().isValid());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    void testCreateSocketWithLocalAddressAndPort() {
        try {
            int localPort = 51510;
            SecureSSLSocket secureSocket = (SecureSSLSocket) factory.createSocket("localhost", 8443, InetAddress.getLocalHost(), localPort);
            System.out.println("Socket unique id: " + secureSocket.getSession().getId());
//            assertTrue(secureSocket.getSession().isValid());
            assertEquals(localPort, secureSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
//    @Test
    void testCreateSocketWithInetAddressAndLocalAddressAndPort() {
        try {
            int localPort = 51511;
            SecureSSLSocket secureSocket = (SecureSSLSocket) factory.createSocket(InetAddress.getByName("localhost"), 8443, InetAddress.getByName("localhost"), localPort);
            System.out.println("Socket unique id: " + secureSocket.getSession().getId());
            assertTrue(secureSocket.getSession().isValid());
            assertEquals(localPort, secureSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }


}