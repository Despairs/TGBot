/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.tg;

import com.despairs.bot.Bot;
import com.despairs.bot.model.TGMessage;
import com.despairs.bot.utils.MessageBuilder;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.iflex.commons.logging.Log4jLogger;

/**
 *
 * @author EKovtunenko
 */
@Singleton
public class MessageSenderImpl implements TGMessageSender {

    private Logger logger = Log4jLogger.getLogger(MessageSenderImpl.class);
    @Inject
    private Bot bot;

    @Override
    public Message sendTGMessage(TGMessage message, Long chatId) {
        return sendTGMessage(message, chatId, null);
    }

    @Override
    public Message sendTGMessage(TGMessage message, String chatId) {
        return sendTGMessage(message, chatId, null);
    }

    @Override
    public Message sendTGMessage(TGMessage message, Long chatId, Integer replyTo) {
        return sendTGMessage(message, String.valueOf(chatId), replyTo);
    }

    @Override
    public Message sendTGMessage(TGMessage message, String chatId, Integer replyTo) {
        logger.debug("Send message: {} to chat {}, replyTo: {}", message, chatId, replyTo);
        Message ret = null;
        try {
            PartialBotApiMethod<Message> msg = MessageBuilder.build(message, chatId, replyTo);
            switch (message.getType()) {
                case DOCUMENT:
                    URL docUrl = new URL(message.getLink());
                    HttpURLConnection connection = null;
                    try {
                        connection = (HttpURLConnection) docUrl.openConnection();
                        connection.connect();
                        try (InputStream is = connection.getInputStream()) {
                            ((SendDocument) msg).setNewDocument(String.valueOf(System.currentTimeMillis()), is);
                            ret = bot.sendDocument((SendDocument) msg);
                        }
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                    break;
                case PHOTO:
                    ret = bot.sendPhoto((SendPhoto) msg);
                    break;
                case TEXT:
                case VIDEO:
                    ret = bot.sendApiMethod((SendMessage) msg);
                    break;
            }
        } catch (Exception ex) {
            Log4jLogger.getLogger(this.getClass()).error(ex);
        }
        return ret;
    }

    @Override
    public void executeMethod(BotApiMethod method) {
        try {
            bot.execute(method);
        } catch (TelegramApiException ex) {
            Log4jLogger.getLogger(this.getClass()).error(ex);
        }
    }
}
