/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot;

import com.despairs.telegram.bot.model.TGMessage;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
public interface TGMessageSender {

    Message sendTGMessage(TGMessage message, String chatId);
    
    Message sendTGMessage(TGMessage message, Long chatId);
    
    Message sendTGMessage(TGMessage message, Long chatId, Integer replyTo);

    Message sendTGMessage(TGMessage message, String chatId, Integer replyTo);
}
