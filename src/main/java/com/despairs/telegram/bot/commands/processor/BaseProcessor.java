/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.processor;

import com.despairs.telegram.bot.TGMessageSender;
import com.despairs.telegram.bot.commands.Command;
import com.despairs.telegram.bot.commands.Scope;
import com.despairs.telegram.bot.commands.ScopeType;
import com.despairs.telegram.bot.commands.registry.CommandRegistry;
import com.despairs.telegram.bot.keyboard.CommandKeyboard;
import com.despairs.telegram.bot.model.MessageType;
import com.despairs.telegram.bot.model.TGMessage;
import com.despairs.telegram.bot.model.User;
import com.despairs.telegram.bot.utils.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
public class BaseProcessor implements CommandProcessor {

    protected TGMessageSender sender;
    protected final Message message;
    
    protected User user;

    public BaseProcessor(Message message) {
        this.message = message;
    }

    @Override
    public CommandProcessor bindSender(TGMessageSender sender) {
        this.sender = sender;
        return this;
    }

    @Override
    public CommandProcessor bindUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public void process() {
        Command command = CommandRegistry.getInstance().getCommand(message.getText());
        if (command != null) {

            List<TGMessage> result = isAllowed(command) ? command.invoke(message) : Arrays.asList(getAccessDeniedMessage());

            result.forEach(m -> {
                if (m.getKeyboard() == null) {
                    try {
                        m.setKeyboard(new CommandKeyboard(user));
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                sender.sendTGMessage(m, message.getChatId());
            });
        }
    }

    private boolean isAllowed(Command command) {
        boolean ret = Boolean.TRUE;
        Annotation scope = AnnotationUtils.findAnnotation(command.getClass(), Scope.class);
        if (scope != null) {
            Scope _scope = (Scope) scope;
            if (_scope.type().equals(ScopeType.ADMIN)) {
                if (user == null || !user.isAdmin()) {
                    ret = Boolean.FALSE;
                }
            }
        }
        return ret;
    }

    private TGMessage getAccessDeniedMessage() {
        TGMessage ret = new TGMessage(MessageType.TEXT);
        ret.setText("Исполнение команды запрещено");
        return ret;
    }

}
