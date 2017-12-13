/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.utils;

import com.despairs.bot.model.TGMessage;
import org.telegram.telegrambots.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
public class MessageBuilder {

    public static PartialBotApiMethod<Message> build(TGMessage message, String chatId, Integer replyTo) {
        PartialBotApiMethod<Message> ret = null;
        switch (message.getType()) {
            case DOCUMENT:
                ret = createDocumentMessage(message, chatId, replyTo);
                break;
            case PHOTO:
                ret = createPhotoMessage(message, chatId, replyTo);
                break;
            case TEXT:
            case VIDEO:
                ret = createTextMessage(message, chatId, replyTo);
                break;
        }
        return ret;
    }

    private static SendMessage createTextMessage(TGMessage message, String chatId, Integer replyTo) {
        String msg = message.getText();
        if (msg == null) {
            msg = "";
        }
        if (message.getLink() != null) {
            msg += "\n" + message.getLink();
        }
        return new SendMessage()
                .setChatId(chatId)
                .setReplyMarkup(message.getKeyboard())
                .setReplyToMessageId(replyTo == null ? message.getReplyTo() : replyTo)
                .setParseMode(message.getParseMode() != null ? message.getParseMode().name() : null)
                .setText(msg);
    }

    private static SendPhoto createPhotoMessage(TGMessage message, String chatId, Integer replyTo) {
        return new SendPhoto()
                .setChatId(chatId)
                .setReplyMarkup(message.getKeyboard())
                .setReplyToMessageId(replyTo)
                .setPhoto(message.getLink())
                .setCaption(message.getText());
    }

    private static SendDocument createDocumentMessage(TGMessage message, String chatId, Integer replyTo) {
        return new SendDocument()
                .setChatId(chatId)
                .setReplyMarkup(message.getKeyboard())
                .setReplyToMessageId(replyTo)
                .setCaption(message.getText());
    }

}