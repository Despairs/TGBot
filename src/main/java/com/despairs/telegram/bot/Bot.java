/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot;

import com.despairs.telegram.bot.commands.impl.BurgerKingCommand;
import com.despairs.telegram.bot.commands.impl.JobActionCommand;
import com.despairs.telegram.bot.commands.impl.KfcCommand;
import com.despairs.telegram.bot.commands.impl.SalaryActionCommand;
import com.despairs.telegram.bot.commands.impl.SalaryStartCommand;
import com.despairs.telegram.bot.commands.processor.CommandProcessor;
import com.despairs.telegram.bot.commands.processor.CommandProcessorFactory;
import com.despairs.telegram.bot.commands.registry.CommandRegistry;
import com.despairs.telegram.bot.db.repo.SettingsRepository;
import com.despairs.telegram.bot.db.repo.UserRepository;
import com.despairs.telegram.bot.db.repo.impl.SettingsRepositoryImpl;
import com.despairs.telegram.bot.db.repo.impl.UserRepositoryImpl;
import com.despairs.telegram.bot.model.Settings;
import com.despairs.telegram.bot.utils.MessageBuilder;
import com.despairs.telegram.bot.model.TGMessage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 *
 * @author EKovtunenko
 */
public class Bot extends TelegramLongPollingBot implements TGMessageSender {

    private final SettingsRepository settings = SettingsRepositoryImpl.getInstance();
    private final CommandRegistry registry = CommandRegistry.getInstance();
    private final UserRepository users = UserRepositoryImpl.getInstance();

    private final String token;
    private final String botDisplayName;
    public static String BOT_USER_NAME;

    public Bot() throws SQLException, TelegramApiException {
        token = settings.getValueV(Settings.BOT_TOKEN);
        botDisplayName = settings.getValueV(Settings.BOT_NAME);

        BOT_USER_NAME = String.format("@%s ", getMe().getUserName());

        registry.registerCommand("Burger King", new BurgerKingCommand());
        registry.registerCommand("KFC", new KfcCommand());
        registry.registerCommand("ЗП", new SalaryStartCommand());
        registry.registerCommand("SALARY", new SalaryActionCommand());
    }

    @Override
    public void onUpdateReceived(Update update) {
        com.despairs.telegram.bot.model.User user = resolveUser(update);
        CommandProcessor processor = CommandProcessorFactory.getInstance().create(update);
        if (processor != null) {
            processor.bindSender(this).bindUser(user).process();
        }
    }

    private com.despairs.telegram.bot.model.User resolveUser(Update update) {
        com.despairs.telegram.bot.model.User ret = null;
        Message message = update.getMessage();
        Message channelPost = update.getChannelPost();
        CallbackQuery callbackQuery = update.getCallbackQuery();

        User user = message != null ? message.getFrom()
                : channelPost != null ? channelPost.getFrom()
                        : callbackQuery != null ? callbackQuery.getFrom() : null;

        if (user != null) {
            try {
                if (!users.isUserRegistered(user.getId())) {
                    users.registerUser(user.getId(), user.getUserName());
                }
                ret = users.getUser(user.getId());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Can't detect user: " + update);
        }
        return ret;
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
        return botDisplayName;
    }

    @Override
    public void executeMethod(BotApiMethod method) {
        try {
            super.execute(method);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }
}
