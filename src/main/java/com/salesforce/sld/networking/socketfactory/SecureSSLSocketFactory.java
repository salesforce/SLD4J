/*
 * Copyright (c) 2020, salesforce.com, inc. All rights reserved. SPDX-License-Identifier: BSD-3-Clause For full license
 * text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.sld.networking.socketfactory;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import javax.annotation.Nonnull;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Secure SSLSocketFactory wrapper. It implements a set of features on the
 * underlying SSL socket that enforce best security practices. This wrapper
 * uses SSL parameters to set security features on underlying SSL socket.
 * These features cannot be overwritten.
 * @author akubitkar
 *
 */
public class SecureSSLSocketFactory extends SSLSocketFactory {
    private SSLSocketFactory theRealFactory;

    /**
     * Create a SSL socket factory instance using the getDefault method
     * of the SSLSocketFactory.
     */
    public SecureSSLSocketFactory() {
        theRealFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    }

    /**
     * Create a SSL socket factory instance using the SSL context. SSL context is
     * initialized using keystore, truststore or both. SSL context uses default random
     * function for initialization.
     * @param keyStore Custom keystore to be used for client authentication
     * @param keystorePass Keystore credential to be used for client socket creation
     * @param trustStore Custom truststore to be used instead of default cacerts shipped with JDK.
     */
    public SecureSSLSocketFactory(
            KeyStore keyStore,
            String keystorePass,
            KeyStore trustStore) {
        try {
            X509KeyManager x509KeyManager = null;
            X509TrustManager x509TrustManager = null;
            if (keyStore != null) {
                if (!keystorePass.isEmpty()) {
                    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                    kmf.init(keyStore, keystorePass.toCharArray());
                    for (KeyManager keyManager : kmf.getKeyManagers()) {
                        if (keyManager instanceof X509KeyManager) {
                            x509KeyManager = (X509KeyManager) keyManager;
                            break;
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Keystore password cannot be empty");
                }
            }
            if (trustStore != null) {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
                tmf.init(trustStore);
                for (TrustManager trustManager : tmf.getTrustManagers()) {
                    if (trustManager instanceof X509TrustManager) {
                        x509TrustManager = (X509TrustManager) trustManager;
                        break;
                    }
                }
            }
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            if (x509KeyManager == null && x509TrustManager == null) {
                throw new IllegalArgumentException("KeyStore and TrustStore both arguments are null."
                        + " Instantiate using the default constructor instead");
            } else {
                if (x509KeyManager == null) {
                    sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
                } else if (x509TrustManager == null) {
                    sslContext.init(new KeyManager[]{x509KeyManager}, null, null);
                } else {
                    sslContext.init(new KeyManager[]{x509KeyManager}, new TrustManager[]{x509TrustManager}, null);
                }
                theRealFactory = sslContext.getSocketFactory();
            }
        }
        catch (IllegalStateException | GeneralSecurityException exception) {
            exception.printStackTrace();
        }
    }

    private SecureSSLSocket getSecureSocket(SSLSocket socket) {
        Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
        SSLParameters params = new SSLParameters();
        Map<String, Object> obj = null;
        try {
            InputStream conf = getClass().getClassLoader().getResourceAsStream("config.yaml");
            obj = yaml.load(conf);
            params.setEndpointIdentificationAlgorithm((String) obj.get("EndpointIdentificationAlgorithm"));
            ArrayList<String> arrayList1 = (ArrayList<String>) obj.get("EnabledProtocols");
            String[] protocols = arrayList1.toArray(new String[arrayList1.size()]);
            params.setProtocols(protocols);
            ArrayList<String> arrayList2 = (ArrayList<String>) obj.get("CipherSuites");
            String[] cipherSuites = arrayList2.toArray(new String[arrayList2.size()]);
            params.setCipherSuites(cipherSuites);
        }
        catch (NullPointerException | ClassCastException e){
            e.printStackTrace();
        }
        return new SecureSSLSocket(socket, params);
    }

    @Override
    public Socket createSocket() throws IOException {
        SSLSocket socket = (SSLSocket) theRealFactory.createSocket();
        SecureSSLSocket secureSocket = getSecureSocket(socket);
        return secureSocket;
    }

    @Override
    public Socket createSocket(
            String host,
            int port) throws IOException {
        SSLSocket socket = (SSLSocket) theRealFactory.createSocket(host, port);
        SecureSSLSocket secureSocket = getSecureSocket(socket);
        return secureSocket;
    }

    @Override
    public Socket createSocket(
            String host,
            int port,
            InetAddress localHost,
            int localPort) throws IOException {
        SSLSocket socket = (SSLSocket) theRealFactory.createSocket(host, port, localHost, localPort);
        SecureSSLSocket secureSocket = getSecureSocket(socket);
        return secureSocket;

    }

    @Override
    public Socket createSocket(
            InetAddress host,
            int port) throws IOException {
        SSLSocket socket = (SSLSocket) theRealFactory.createSocket(host, port);
        SecureSSLSocket secureSocket = getSecureSocket(socket);
        return secureSocket;
    }

    @Override
    public Socket createSocket(
            InetAddress address,
            int port,
            InetAddress localAddress,
            int localPort) throws IOException {
        SSLSocket socket = (SSLSocket) theRealFactory.createSocket(address, port, localAddress, localPort);
        SecureSSLSocket secureSocket = getSecureSocket(socket);
        return secureSocket;
    }

    @Override
    public Socket createSocket(
            Socket s,
            String host,
            int port,
            boolean autoClose) throws IOException {
        SSLSocket socket = (SSLSocket) theRealFactory.createSocket(s,host, port, autoClose);
        SecureSSLSocket secureSocket = getSecureSocket(socket);
        return secureSocket;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return theRealFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return theRealFactory.getSupportedCipherSuites();
    }

}