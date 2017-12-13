/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot;

import com.despairs.bot.tg.TGMessageSender;
import com.despairs.bot.db.repo.SettingsRepository;
import com.despairs.bot.model.Settings;
import com.despairs.bot.model.TGMessage;
import com.despairs.bot.producer.MessageProducer;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.telegram.telegrambots.api.objects.Message;
import ru.iflex.commons.logging.Log4jLogger;

/**
 *
 * @author EKovtunenko
 */
@Singleton
@LocalBean
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Lock(LockType.READ)
public class Scheduler {

    private org.apache.logging.log4j.Logger logger = Log4jLogger.getLogger(Scheduler.class);

    @Inject
    @Any
    private Instance<MessageProducer> producers;

    @Inject
    private TGMessageSender sender;

    @Inject
    private SettingsRepository settings;

    private String defaultChannelId;

    @PostConstruct
    private void init() {
        try {
            if (settings != null) {
                defaultChannelId = settings.getValueV(Settings.DEFAULT_CHANNEL_ID);
            }
        } catch (SQLException ex) {
            logger.error(ex);
        }
    }

    @Schedule(hour = "*", minute = "*/10", persistent = false)
    private void doScheduleAction() {
        Log4jLogger.pushThread();
        if (settings == null) {
            init();
        } else {
            Date date = new Date();
            logger.info("Check for new messages");
            producers.forEach(producer -> {
                try {
                    List<TGMessage> messages = producer.produce();
                    if (!messages.isEmpty()) {
                        logger.info("Got {} messages from producer {}", messages.size(), producer.getClass().getSimpleName());
                        Map<TGMessage, Integer> sendedMessages = new HashMap<>();
                        messages.forEach(m -> {
                            Integer replyTo = null;
                            if (m.getRef() != null) {
                                replyTo = sendedMessages.get(m.getRef());
                            }
                            String _chatId = m.getChatId() != null ? m.getChatId() : defaultChannelId;
                            if (replyTo == null && m.getRef() != null) {
                                Message ret = sender.sendTGMessage(m.getRef(), _chatId, replyTo);
                                replyTo = ret.getMessageId();
                                sendedMessages.put(m.getRef(), replyTo);
                            }
                            Message ret = sender.sendTGMessage(m, _chatId, replyTo);
                            if (ret != null) {
                                sendedMessages.put(m, ret.getMessageId());
                            }
                        });
                    }
                } catch (Exception ex) {
                    logger.error(ex);
                }
            });
        }
    }
}
