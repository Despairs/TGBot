/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.impl;

import com.despairs.telegram.bot.TGMessageSender;
import com.despairs.telegram.bot.keyboard.JobKeyboard;
import com.despairs.telegram.bot.keyboard.JobKeyboard;
import com.despairs.telegram.bot.model.TGMessage;
import com.despairs.telegram.bot.model.User;
import java.util.Collections;
import java.util.List;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
public class JobInlineCommand extends InlineCommand {

    private final User user;

    public JobInlineCommand(CallbackQuery callback, TGMessageSender sender, User user) {
        super(callback, sender);
        this.user = user;
    }

    @Override
    public List<TGMessage> invoke(Message message) {
        String callbackMessage = getCallbackMessage();
        int keyboardType = Integer.MIN_VALUE;
        switch (callbackMessage) {
            case "JOB#INPUT":
                keyboardType = JobKeyboard.INPUT;
                break;
            case "JOB#RESET":
                keyboardType = JobKeyboard.MAIN;
                break;
            case "JOB#HIDE":
                deleteMessage();
                break;
            case "JOB#VIEW":
                keyboardType = JobKeyboard.VIEW;
                break;
            case "JOB#VIEW#ALL":
                keyboardType = JobKeyboard.VIEW_ALL;
                break;
            case "JOB#VIEW#AGGREGATION":
                keyboardType = JobKeyboard.VIEW_AGGREGATION;
                break;
            case "JOB#VIEW#AGGREGATION#ALL_TIME":
                keyboardType = JobKeyboard.VIEW_AGGREGATION_ALL_TIME;
                break;
            case "JOB#VIEW#AGGREGATION#LAST_PAYMENT":
                keyboardType = JobKeyboard.VIEW_AGGREGATION_LAST_PAYMENT;
                break;
            case "JOB#VIEW#ALL#ALL_TIME":
                keyboardType = JobKeyboard.VIEW_ALL_ALL_TIME;
                break;
            case "JOB#VIEW#ALL#LAST_PAYMENT":
                keyboardType = JobKeyboard.VIEW_ALL_LAST_PAYMENT;
                break;
        }
        if (keyboardType != Integer.MIN_VALUE) {
            changeKeyboard(new JobKeyboard(keyboardType, user));
        }
        return Collections.EMPTY_LIST;
    }

}
