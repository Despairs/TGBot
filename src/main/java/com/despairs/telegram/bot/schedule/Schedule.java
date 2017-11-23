/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.schedule;

import com.despairs.telegram.bot.TGMessageSender;
import com.despairs.telegram.bot.db.repo.SettingsRepository;
import com.despairs.telegram.bot.db.repo.impl.SettingsRepositoryImpl;
import com.despairs.telegram.bot.model.Settings;
import com.despairs.telegram.bot.model.TGMessage;
import com.despairs.telegram.bot.producer.MessageProducer;
import com.despairs.telegram.bot.producer.MiuiProducer;
import com.despairs.telegram.bot.producer.NewXboxOneProducer;
import com.despairs.telegram.bot.producer.RedmineIssueProducer;
import com.despairs.telegram.bot.producer.VkWallpostProducer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
public class Schedule implements Runnable {

     private final SettingsRepository settings = SettingsRepositoryImpl.getInstance();
    
    private final TGMessageSender sender;
    private final String chatId;

    private final List<MessageProducer> producers = new ArrayList<>();

    public Schedule(TGMessageSender sender) throws SQLException {
        this.sender = sender;
        this.chatId = settings.getValueV(Settings.DEFAULT_CHANNEL_ID);
        registerProducers();
    }

    private void registerProducers() throws SQLException {
        producers.add(new NewXboxOneProducer());
        producers.add(new MiuiProducer());
        producers.add(new VkWallpostProducer("elistratov"));
        producers.add(new RedmineIssueProducer());
    }

    @Override
    public void run() {
        Date date = new Date();
        System.out.println(date + ": Check for new messages");
        producers.parallelStream().forEach(producer -> {
            try {
                List<TGMessage> messages = producer.produce();
                if (!messages.isEmpty()) {
                    System.out.println(date + String.format(": Got %d messages from producer %s", messages.size(), producer.getClass().getSimpleName()));
                    Map<TGMessage, Integer> sendedMessages = new HashMap<>();
                    messages.forEach(m -> {
                        Integer replyTo = null;
                        if (m.getRef() != null) {
                            replyTo = sendedMessages.get(m.getRef());
                        }
                        String _chatId = m.getChatId() != null ? m.getChatId() : chatId;
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
                ex.printStackTrace();
            }
        });
    }

}
