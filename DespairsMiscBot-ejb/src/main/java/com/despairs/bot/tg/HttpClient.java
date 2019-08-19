package com.despairs.bot.tg;

import com.despairs.bot.db.repo.SettingsRepository;
import com.despairs.bot.model.Settings;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import ru.iflex.commons.logging.Log4jLogger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static org.telegram.telegrambots.Constants.SOCKET_TIMEOUT;

/**
 * @author EKovtunenko
 */
@Singleton
public class HttpClient {

    private static final RequestConfig config = RequestConfig.copy(RequestConfig.custom().build())
            .setSocketTimeout(SOCKET_TIMEOUT)
            .setConnectTimeout(SOCKET_TIMEOUT)
            .setConnectionRequestTimeout(SOCKET_TIMEOUT).build();

    private static SSLContext sslContext;

    static {
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
    }

    private static final HttpClientBuilder BUILDER = HttpClientBuilder.create()
            .setSSLHostnameVerifier(new NoopHostnameVerifier())
            .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
            .setConnectionTimeToLive(70, TimeUnit.SECONDS)
            .setMaxConnTotal(100);

    @Inject
    private SettingsRepository settings;

    private CloseableHttpClient buildClient() {
        try {
            String host = settings.getValueV(Settings.PROXY_HOST);
            if (host != null) {
                Long port = settings.getValueN(Settings.PROXY_PORT);
                BUILDER.setProxy(new HttpHost(host, port.intValue()));
            }
        } catch (SQLException ex) {
            Log4jLogger.getLogger(HttpClient.class).error(ex);
        }
        return BUILDER.build();
    }

    public CloseableHttpResponse execute(HttpRequestBase request) throws IOException {
        request.setConfig(config);
        return buildClient().execute(request);
    }

}
