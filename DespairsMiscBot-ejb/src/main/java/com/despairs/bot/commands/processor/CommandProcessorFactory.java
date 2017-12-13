/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.commands.processor;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

/**
 *
 * @author EKovtunenko
 */
@Singleton
public class CommandProcessorFactory {

    @Inject
    private Instance<CommandProcessor> processors;

    public CommandProcessor create(Update update) {
        Class processorClass = null;
        Message message = null;
        if (update.hasCallbackQuery()) {
            processorClass = InlineCallbackQueryProcessor.class;
            message = update.getCallbackQuery().getMessage();
        } else if (update.hasChannelPost()) {
            processorClass = ChannelProcessor.class;
            message = update.getChannelPost();
        } else if (update.hasMessage()) {
            message = update.getMessage();
            if (message != null && (message.isGroupMessage() || message.isSuperGroupMessage())) {
                processorClass = GroupCommandMessageProcessor.class;
            } else {
                processorClass = BaseProcessor.class;
            }
        }

        if (processorClass == null) {
            throw new IllegalArgumentException("Can't detect processor for " + update);
        }

        CommandProcessor processor = getInstance(processorClass).bindMessage(message);

        if (processor instanceof InlineCallbackQueryProcessor) {
            InlineCallbackQueryProcessor _p = (InlineCallbackQueryProcessor) processor;
            _p.setCallbackQuery(update.getCallbackQuery());
        }

        return processor;
    }

    private CommandProcessor getInstance(Class clazz) {
        CommandProcessor ret = null;
        for (CommandProcessor processor : processors) {
            if (processor.getClass().equals(clazz)) {
                ret = processor;
                break;
            }
        }
        return ret;
    }
}
