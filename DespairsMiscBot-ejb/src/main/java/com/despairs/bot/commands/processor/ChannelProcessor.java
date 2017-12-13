/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.commands.processor;

import com.despairs.bot.model.MessageType;
import com.despairs.bot.model.TGMessage;

/**
 *
 * @author EKovtunenko
 */
public class ChannelProcessor extends BaseProcessor {

    private static final String RESPONSE = "Обновляю все задания";

    @Override
    public void process() {
        TGMessage msg = new TGMessage(MessageType.TEXT);
        msg.setText(RESPONSE);
        sender.sendTGMessage(msg, message.getChatId(), message.getMessageId());
        //@@TODO сделать обновление
//        ScheduleRegistry.getInstance().runAll();
    }

}
