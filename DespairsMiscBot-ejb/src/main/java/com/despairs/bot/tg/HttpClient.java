package com.despairs.bot.tg;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.telegram.telegrambots.Constants.SOCKET_TIMEOUT;

/**
 * @author EKovtunenko
 */
public class HttpClient {

    private static final String PROXY_HOST = "89.236.17.106";
    private static final int PROXY_PORT = 3128;
    private static final HttpClient INSTANCE = new HttpClient();

    private final CloseableHttpClient client;
    private final RequestConfig config;

    private HttpClient() {
        this.client = HttpClientBuilder.create()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setConnectionTimeToLive(70, TimeUnit.SECONDS)
                .setMaxConnTotal(100)
                .setProxy(new HttpHost(PROXY_HOST, PROXY_PORT))
                .build();
        this.config = RequestConfig.copy(RequestConfig.custom().build())
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectTimeout(SOCKET_TIMEOUT)
                .setProxy(new HttpHost(PROXY_HOST, PROXY_PORT))
                .setConnectionRequestTimeout(SOCKET_TIMEOUT).build();
    }

    public static HttpClient getInstance() {
        return INSTANCE;
    }

    public CloseableHttpResponse execute(HttpRequestBase request) throws IOException, ClientProtocolException {
        request.setConfig(this.config);
        return client.execute(request);
    }

    public CloseableHttpClient getClient() {
        return client;
    }

    public RequestConfig getConfig() {
        return config;
    }
}
