/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.commands.processor;

import com.despairs.bot.tg.TGMessageSender;
import com.despairs.bot.commands.Command;
import com.despairs.bot.commands.CommandRegistry;
import com.despairs.bot.keyboard.CommandKeyboard;
import com.despairs.bot.model.MessageType;
import com.despairs.bot.model.TGMessage;
import com.despairs.bot.model.User;
import com.despairs.bot.utils.CommandHelper;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.objects.Message;
import ru.iflex.commons.logging.Log4jLogger;

/**
 *
 * @author EKovtunenko
 */
public class BaseProcessor implements CommandProcessor {

    private Logger logger = Log4jLogger.getLogger(this.getClass());

    @Inject
    private CommandRegistry commandRegistry;

    @Inject
    protected TGMessageSender sender;

    protected Message message;
    protected User user;

    @Override
    public CommandProcessor bindUser(User user) {
        this.user = user;
        logger.debug("Bind user {}", user);
        return this;
    }

    @Override
    public CommandProcessor bindMessage(Message message) {
        this.message = message;
        logger.debug("Bind message {}", message);
        return this;
    }

    @Override
    public void process() {
        Command command = commandRegistry.getCommand(message.getText());
        if (command != null) {

            List<TGMessage> result = CommandHelper.isAllowedForUser(command, user) ? command.invoke(message) : Arrays.asList(getAccessDeniedMessage());

            result.forEach(m -> {
                if (m.getKeyboard() == null) {
                    m.setKeyboard(new CommandKeyboard(user));
                }
                sender.sendTGMessage(m, message.getChatId());
            });
        }
    }

    private TGMessage getAccessDeniedMessage() {
        TGMessage ret = new TGMessage(MessageType.TEXT);
        ret.setText("Исполнение команды запрещено");
        return ret;
    }

}
