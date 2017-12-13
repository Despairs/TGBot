/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.tg;

import com.despairs.bot.model.TGMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.methods.BotApiMethod;

/**
 *
 * @author EKovtunenko
 */
public interface TGMessageSender {

    Message sendTGMessage(TGMessage message, String chatId);
    
    Message sendTGMessage(TGMessage message, Long chatId);
    
    Message sendTGMessage(TGMessage message, Long chatId, Integer replyTo);

    Message sendTGMessage(TGMessage message, String chatId, Integer replyTo);
    
    void executeMethod(BotApiMethod method);
}
