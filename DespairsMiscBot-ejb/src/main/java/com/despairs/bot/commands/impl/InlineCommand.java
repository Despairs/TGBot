/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.commands.impl;

import com.despairs.bot.tg.TGMessageSender;
import com.despairs.bot.commands.Command;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 *
 * @author EKovtunenko
 */
public abstract class InlineCommand implements Command {

    protected final CallbackQuery callback;
    protected final TGMessageSender sender;

    public InlineCommand(CallbackQuery callback, TGMessageSender sender) {
        this.callback = callback;
        this.sender = sender;
    }

    protected Integer getMessageId() {
        return callback.getMessage().getMessageId();
    }

    protected Long getChatId() {
        return callback.getMessage().getChatId();
    }

    protected String getCallbackMessage() {
        return callback.getData();
    }

    protected void changeKeyboard(InlineKeyboardMarkup keyboard) {
        EditMessageReplyMarkup editKeyboard = new EditMessageReplyMarkup()
                .setChatId(getChatId())
                .setMessageId(getMessageId())
                .setReplyMarkup(keyboard);
        sender.executeMethod(editKeyboard);
    }

    protected void deleteMessage() {
        DeleteMessage deleteMessage = new DeleteMessage()
                .setChatId(String.valueOf(getChatId()))
                .setMessageId(getMessageId());
        sender.executeMethod(deleteMessage);
    }

}
