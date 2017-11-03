/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.utils;

import java.security.KeyManagementException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

/**
 *
 * @author EKovtunenko
 */
public class HttpsUtils {

    public static void trustAllCerts() {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new AnyHostVerifier()}, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
    }

    public static SSLConnectionSocketFactory getSSLConnectionSocketFactory() {
        try {
            //Игнорирование сертификатов
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new X509TrustManager[]{new AnyHostVerifier()}, new java.security.SecureRandom());
            return new SSLConnectionSocketFactory(sc, (String string, SSLSession ssls) -> true);
        } catch (Exception e) {
        }
        return null;
    }
}
