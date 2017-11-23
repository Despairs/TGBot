/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot;

import com.despairs.telegram.bot.commands.impl.BurgerKingCommand;
import com.despairs.telegram.bot.commands.impl.KfcCommand;
import com.despairs.telegram.bot.commands.processor.CommandProcessor;
import com.despairs.telegram.bot.commands.processor.CommandProcessorFactory;
import com.despairs.telegram.bot.commands.registry.CommandRegistry;
import com.despairs.telegram.bot.db.repo.SettingsRepository;
import com.despairs.telegram.bot.db.repo.impl.SettingsRepositoryImpl;
import com.despairs.telegram.bot.model.Settings;
import com.despairs.telegram.bot.utils.MessageBuilder;
import com.despairs.telegram.bot.model.TGMessage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import org.telegram.telegrambots.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

/**
 *
 * @author EKovtunenko
 */
public class Bot extends TelegramLongPollingBot implements TGMessageSender {

    private final SettingsRepository settings = SettingsRepositoryImpl.getInstance();

    private final String token;
    private final String botName;

    public Bot() throws SQLException {
        token = settings.getValueV(Settings.BOT_TOKEN);
        botName = settings.getValueV(Settings.BOT_NAME);
        CommandRegistry.getInstance().registerCommand("Burger King", new BurgerKingCommand());
        CommandRegistry.getInstance().registerCommand("KFC", new KfcCommand());
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if ((message != null && !message.isGroupMessage()) || message == null) {
            CommandProcessor processor = CommandProcessorFactory.getInstance().create(update);
            processor.bindSender(this).process();
        }
    }

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
                            ret = sendDocument((SendDocument) msg);
                        }
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                    break;
                case PHOTO:
                    ret = sendPhoto((SendPhoto) msg);
                    break;
                case TEXT:
                case VIDEO:
                    ret = sendApiMethod((SendMessage) msg);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
}
