/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.impl;

import com.despairs.telegram.bot.commands.Command;
import com.despairs.telegram.bot.model.MessageType;
import com.despairs.telegram.bot.model.TGMessage;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
public class StartCommand implements Command {

    private static final String RESPONSE_PATTERN = "Добро пожаловать, %s!";

    @Override
    public List<TGMessage> invoke(Message message) {
        List<TGMessage> ret = new ArrayList<>();
        TGMessage msg = new TGMessage(MessageType.TEXT);
        msg.setText(String.format(RESPONSE_PATTERN, message.getFrom().getFirstName()));
        ret.add(msg);
        return ret;
    }

}
