/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.model;

import lombok.Data;
import lombok.ToString;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;

/**
 * @author EKovtunenko
 */
@Data
@ToString
public class TGMessage {

    private String link;
    private String text;
    private MessageType type;
    private TGMessage ref;
    private ReplyKeyboard keyboard;
    private ParseMode parseMode;
    private String chatId;
    private Integer replyTo;

    public TGMessage(MessageType type) {
        this.type = type;
    }

}
