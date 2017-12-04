/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.impl;

import com.despairs.telegram.bot.TGMessageSender;
import com.despairs.telegram.bot.keyboard.SalaryKeyboard;
import com.despairs.telegram.bot.model.TGMessage;
import java.util.Collections;
import java.util.List;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
public class SalaryInlineCommand extends InlineCommand {

    public SalaryInlineCommand(CallbackQuery callback, TGMessageSender sender) {
        super(callback, sender);
    }

    @Override
    public List<TGMessage> invoke(Message message) {
        String callbackMessage = getCallbackMessage();
        switch (callbackMessage) {
            case "SALARY#INPUT":
                changeKeyboard(new SalaryKeyboard(SalaryKeyboard.INPUT));
                break;
            case "SALARY#RESET":
                changeKeyboard(new SalaryKeyboard(SalaryKeyboard.MAIN));
                break;
            case "SALARY#HIDE":
                deleteMessage();
                break;
            case "SALARY#VIEW":
                changeKeyboard(new SalaryKeyboard(SalaryKeyboard.VIEW));
                break;
            case "SALARY#VIEW#ALL":
                changeKeyboard(new SalaryKeyboard(SalaryKeyboard.VIEW_ALL));
                break;
            case "SALARY#VIEW#AGGREGATION":
                changeKeyboard(new SalaryKeyboard(SalaryKeyboard.VIEW_AGGREGATION));
                break;
            default:
                break;
        }
        return Collections.EMPTY_LIST;
    }

}
