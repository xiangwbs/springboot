/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package com.xwbing.util.wxpay;

import com.xwbing.exception.BusinessException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;

/**
 * This example demonstrates how to create secure connections with a custom SSL
 * context.
 */
public class ClientCustomSSL {

    public static CloseableHttpClient getCloseableHttpClient(String mchId) {
        FileInputStream instream = null;
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("PKCS12");
            String path = ClientCustomSSL.class.getClassLoader().getResource("wx_apiclient_cert.p12").getPath();
            instream = new FileInputStream(new File(path));
            keyStore.load(instream, mchId.toCharArray());
        } catch (Exception e) {
            throw new BusinessException("读取微信证书出错!");
        } finally {
            if (instream != null) {
                try {
                    instream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Trust own CA and all self-signed certs
            SSLContext sslcontext = null;
            try {
                sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mchId.toCharArray())
                        .build();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            }
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1"},
                    null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            return HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();
        }
    }

    public static void main(String[] args) {
        String path = Thread.currentThread().getContextClassLoader().getResource("wx_apiclient_cert.p12")
                .getPath();
        System.out.println(path);
    }
}
