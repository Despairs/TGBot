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
import java.sql.SQLException;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import ru.iflex.commons.logging.Log4jLogger;

/**
 *
 * @author EKovtunenko
 */
@ApplicationScoped
public class Bot extends TelegramLongPollingBot {

    private Logger logger = Log4jLogger.getLogger(Bot.class);
    
    @Inject
    private SettingsRepository settings;
    @Inject
    private UserRepository users;
    @Inject
    private CommandProcessorFactory processorFactory;

    private String token;
    private String botDisplayName;
    public static String BOT_USER_NAME;

    @PostConstruct
    public void init() {
        try {
            token = settings.getValueV(Settings.BOT_TOKEN);
            botDisplayName = settings.getValueV(Settings.BOT_NAME);
            BOT_USER_NAME = String.format("@%s ", getMe().getUserName());
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.trace(update);
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
                    users.registerUser(user.getId(), user.getUserName());
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

}
