/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.commands.processor;

import com.despairs.bot.Bot;
import com.despairs.bot.commands.Command;
import com.despairs.bot.commands.impl.JobActionCommand;
import com.despairs.bot.commands.impl.JobStartCommand;
import com.despairs.bot.commands.impl.RegisterRedmineUserCommand;
import com.despairs.bot.model.TGMessage;
import java.util.List;

/**
 *
 * @author EKovtunenko
 */
public class GroupCommandMessageProcessor extends BaseProcessor {

    @Override
    public void process() {
        Command command = null;
        String commandText = message.getText();
        if (commandText != null) {
            if (commandText.contains(Bot.BOT_USER_NAME)) {
                commandText = commandText.replace(Bot.BOT_USER_NAME, "");
            }
            if (commandText.startsWith("/redmine")) {
                command = new RegisterRedmineUserCommand();
            } else if (commandText.startsWith("/job")) {
                command = new JobStartCommand(user);
            } else if (commandText.startsWith("JOB#")) {
                command = new JobActionCommand(user);
            }
            if (command != null) {
                List<TGMessage> result = command.invoke(message);
                result.forEach(m -> {
                    sender.sendTGMessage(m, message.getChatId(), message.getMessageId());
                });
            }
        }
    }

}
