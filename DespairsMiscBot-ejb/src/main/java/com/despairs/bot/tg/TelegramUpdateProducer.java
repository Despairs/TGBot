/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.tg;

import com.despairs.bot.Bot;
import com.despairs.bot.fork.SerialazibleGetUpdates;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.telegram.telegrambots.ApiConstants;
import org.telegram.telegrambots.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ru.iflex.commons.logging.Log4jLogger;

import javax.ejb.Singleton;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * @author EKovtunenko
 */
@Singleton
public class TelegramUpdateProducer {

    @Inject
    private Bot bot;

    private GetUpdates request;

    private int lastReceivedUpdate = 0;

    public List<Update> produce() {
        List<Update> updates = Collections.EMPTY_LIST;
        try {
            request = buildRequest();
            HttpPost httpPost = buildHttpPost(request);
            try (CloseableHttpResponse response = HttpClient.getInstance().execute(httpPost)) {
                updates = extractUpdateList(response);
                updates.removeIf(update -> update.getUpdateId() < lastReceivedUpdate);
                lastReceivedUpdate = updates.stream()
                        .map(Update::getUpdateId)
                        .max(Integer::compareTo)
                        .orElse(0);
            }
        } catch (Exception e) {
            Log4jLogger.getLogger(this.getClass()).error(e);
        }
        return updates;
    }

    private HttpPost buildHttpPost(Object request) {
        String url = bot.getOptions().getBaseUrl() + bot.getBotToken() + "/" + GetUpdates.PATH;
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("charset", StandardCharsets.UTF_8.name());
        httpPost.setEntity(new StringEntity(request.toString(), ContentType.APPLICATION_JSON));
        return httpPost;
    }

    private GetUpdates buildRequest() {
        return new SerialazibleGetUpdates()
                .setLimit(100)
                .setTimeout(ApiConstants.GETUPDATES_TIMEOUT)
                .setOffset(lastReceivedUpdate + 1);
    }

    private List<Update> extractUpdateList(CloseableHttpResponse response) throws IOException, TelegramApiRequestException {
        List<Update> updates = Collections.EMPTY_LIST;
        BufferedHttpEntity buf = new BufferedHttpEntity(response.getEntity());
        String responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8);
        if (response.getStatusLine().getStatusCode() == 200) {
            updates = request.deserializeResponse(responseContent);
        } else {
            Log4jLogger.getLogger(this.getClass()).error("Receive invalid response from TG: {}", responseContent);
        }
        return updates;
    }

}
