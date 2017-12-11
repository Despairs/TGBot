/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.processor;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

/**
 *
 * @author EKovtunenko
 */
public class CommandProcessorFactory {

    private static final CommandProcessorFactory instance = new CommandProcessorFactory();

    public static CommandProcessorFactory getInstance() {
        return instance;
    }

    public CommandProcessor create(Update update) {
        CommandProcessor ret = null;
        Message message = update.getMessage();
        if (update.hasCallbackQuery()) {
            ret = new InlineCallbackQueryProcessor(update);
        } else if (update.getChannelPost() != null) {
            ret = new ChannelProcessor(update);
        } else if (message != null) {
            if ((message.isGroupMessage() || message.isSuperGroupMessage())) {
                ret = new GroupCommandMessageProcessor(update);
            } else {
                ret = new PersonalMessageProcessor(update);
            }
        }
        if (ret == null) {
            throw new IllegalArgumentException("Can't detect processor for " + update);
        }
        return ret;
    }
}
