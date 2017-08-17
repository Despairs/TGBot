/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.schedule;

import com.despairs.telegram.bot.TGMessageSender;
import com.despairs.telegram.bot.model.TGMessage;
import com.despairs.telegram.bot.producer.MessageProducer;
import com.despairs.telegram.bot.producer.MiuiProducer;
import com.despairs.telegram.bot.producer.NewXboxOneProducer;
import com.despairs.telegram.bot.producer.VkWallpostProducer;
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

    private final TGMessageSender sender;
    private final String chatId;

    private final List<MessageProducer> producers = new ArrayList<>();

    public Schedule(TGMessageSender sender, String chatId) {
        this.sender = sender;
        this.chatId = chatId;
        registerProducers();
    }

    private void registerProducers() {
        producers.add(new NewXboxOneProducer());
        producers.add(new MiuiProducer());
        producers.add(new VkWallpostProducer("elistratov"));
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
                    messages.stream().forEach(m -> {
                        Integer replyTo = sendedMessages.get(m.getRef());
                        if (m.getRef() != null && replyTo == null) {
                            Message ret = sender.sendTGMessage(m.getRef(), chatId, replyTo);
                            replyTo = ret.getMessageId();
                            sendedMessages.put(m.getRef(), replyTo);
                        }
                        Message ret = sender.sendTGMessage(m, chatId, replyTo);
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
