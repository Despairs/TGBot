/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.utils;

import javax.net.ssl.X509TrustManager;

/**
 *
 * @author EKovtunenko
 */
public class AnyHostVerifier implements X509TrustManager {

    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    public void checkClientTrusted(
            java.security.cert.X509Certificate[] certs, String authType) {
    }

    public void checkServerTrusted(
            java.security.cert.X509Certificate[] certs, String authType) {
    }
}
