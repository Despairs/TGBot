/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.processor;

import com.despairs.telegram.bot.model.MessageType;
import com.despairs.telegram.bot.model.TGMessage;
import com.despairs.telegram.bot.schedule.ScheduleRegistry;
import org.telegram.telegrambots.api.objects.Update;

/**
 *
 * @author EKovtunenko
 */
public class ChannelProcessor extends BaseProcessor {

    private static final String RESPONSE = "Обновляю все задания";

    public ChannelProcessor(Update update) {
        super(update.getChannelPost());
    }

    @Override
    public void process() {
        TGMessage msg = new TGMessage(MessageType.TEXT);
        msg.setText(RESPONSE);
        sender.sendTGMessage(msg, message.getChatId(), message.getMessageId());
        ScheduleRegistry.getInstance().runAll();
    }

}
