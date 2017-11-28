/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.processor;

import com.despairs.telegram.bot.TGMessageSender;
import com.despairs.telegram.bot.commands.Command;
import com.despairs.telegram.bot.commands.registry.CommandRegistry;
import com.despairs.telegram.bot.keyboard.CommandKeyboard;
import com.despairs.telegram.bot.model.TGMessage;
import java.util.List;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
public class BaseProcessor implements CommandProcessor {

    protected TGMessageSender sender;
    protected final Message message;
    protected final CommandKeyboard keyboard = new CommandKeyboard();

    public BaseProcessor(Message message) {
        this.message = message;
    }

    @Override
    public CommandProcessor bindSender(TGMessageSender sender) {
        this.sender = sender;
        return this;
    }

    @Override
    public void process() {
        Command command = CommandRegistry.getInstance().getCommand(message.getText());
        if (command != null) {
            List<TGMessage> result = command.invoke(message);
            result.forEach(m -> {
                m.setKeyboard(keyboard);
                sender.sendTGMessage(m, message.getChatId());
            });
        }
    }

}
