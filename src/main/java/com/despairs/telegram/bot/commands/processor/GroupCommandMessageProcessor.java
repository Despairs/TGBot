/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.processor;

import com.despairs.telegram.bot.commands.Command;
import com.despairs.telegram.bot.commands.impl.JobCommand;
import com.despairs.telegram.bot.commands.impl.RegisterRedmineUserCommand;
import com.despairs.telegram.bot.commands.registry.CommandRegistry;
import com.despairs.telegram.bot.model.TGMessage;
import java.util.List;
import org.telegram.telegrambots.api.objects.Update;

/**
 *
 * @author EKovtunenko
 */
public class GroupCommandMessageProcessor extends BaseProcessor {

    public GroupCommandMessageProcessor(Update update) {
        super(update.getMessage());
    }

    @Override
    public void process() {
        Command command = null;
        String commandText = message.getText();
        if (commandText.startsWith("/redmine")) {
            command = new RegisterRedmineUserCommand();
        } else if (commandText.startsWith("/job")) {
            command = new JobCommand();
        }
        if (command != null) {
            List<TGMessage> result = command.invoke(message);
            result.forEach(m -> {
                sender.sendTGMessage(m, message.getChatId(), message.getMessageId());
            });
        }
    }

}
