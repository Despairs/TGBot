/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot;

import com.despairs.bot.commands.processor.CommandProcessor;
import com.despairs.bot.commands.processor.CommandProcessorFactory;
import com.despairs.bot.db.repo.SettingsRepository;
import com.despairs.bot.db.repo.UserRepository;
import com.despairs.bot.fork.TelegramLongPollingBot;
import com.despairs.bot.model.Settings;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.iflex.commons.logging.Log4jLogger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.SQLException;

/**
 * @author EKovtunenko
 */
@ApplicationScoped
public class Bot extends TelegramLongPollingBot {

    public static String BOT_USER_NAME = "";
    private Logger logger = Log4jLogger.getLogger(Bot.class);
    @Inject
    private SettingsRepository settings;
    @Inject
    private UserRepository users;
    @Inject
    private CommandProcessorFactory processorFactory;
    private String token;
    private String botDisplayName;

    @PostConstruct
    public void init() {
        try {
            token = settings.getValueV(Settings.BOT_TOKEN);
            botDisplayName = settings.getValueV(Settings.BOT_NAME);
            registerBotName();
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.trace(update);
        if (BOT_USER_NAME.isEmpty()) {
            registerBotName();
        }
        com.despairs.bot.model.User user = resolveUser(update);
        CommandProcessor processor = processorFactory.create(update);
        if (processor != null) {
            processor.bindUser(user).process();
        }
    }

    private com.despairs.bot.model.User resolveUser(Update update) {
        com.despairs.bot.model.User ret = null;
        Message message = update.getMessage();
        Message channelPost = update.getChannelPost();
        CallbackQuery callbackQuery = update.getCallbackQuery();

        User user = message != null ? message.getFrom()
                : channelPost != null ? channelPost.getFrom()
                : callbackQuery != null ? callbackQuery.getFrom() : null;

        if (user != null) {
            try {
                if (!users.isUserRegistered(user.getId())) {
                    users.registerUser(user.getId(), String.format("%s %s (%s)", user.getLastName(), user.getFirstName(), user.getUserName()));
                }
                ret = users.getUser(user.getId());
            } catch (SQLException ex) {
                logger.error(ex);
            }
        } else {
            logger.error("Can't detect user from update {}", update);
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

    private void registerBotName() {
        try {
            BOT_USER_NAME = String.format("@%s ", getMe().getUserName());
        } catch (TelegramApiException ex) {
            logger.error(ex);
        }
    }

}
