/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot;

import com.despairs.telegram.bot.utils.MessageBuilder;
import com.despairs.telegram.bot.producer.VkWallpostProducer;
import com.despairs.telegram.bot.producer.MessageProducer;
import com.despairs.telegram.bot.model.TGMessage;
import com.despairs.telegram.bot.producer.NewXboxOneProducer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class Bot extends TelegramLongPollingBot implements Runnable {

    private final String token;
    private final String chatId;

    private final List<MessageProducer> producers = new ArrayList<>();

    public Bot(String token, String chatId) {
        this.token = token;
        this.chatId = chatId;
        producers.add(new NewXboxOneProducer());
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
                    messages.stream().forEach(m -> {
                        Integer replyTo = null;
                        if (m.getRef() != null) {
                            Message ret = send(m.getRef(), replyTo);
                            replyTo = ret.getMessageId();
                        }
                        send(m, replyTo);
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private Message send(TGMessage message, Integer replyTo) {
        Message ret = null;
        try {
            PartialBotApiMethod<Message> msg = MessageBuilder.build(message, chatId, replyTo);
            switch (message.getType()) {
                case DOCUMENT:
                    ret = sendDocument((SendDocument) msg);
                    break;
                case PHOTO:
                    ret = sendPhoto((SendPhoto) msg);
                    break;
                case TEXT:
                case VIDEO:
                    ret = sendMessage((SendMessage) msg);
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
    public void onUpdateReceived(Update update) {
        run();
    }

    @Override
    public String getBotUsername() {
        return "DespairsTestBot";
    }
}