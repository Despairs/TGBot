/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.commands.impl;

import com.despairs.bot.commands.Command;
import com.despairs.bot.db.repo.UserRepository;
import com.despairs.bot.model.MessageType;
import com.despairs.bot.model.TGMessage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.telegram.telegrambots.api.objects.Message;
import ru.iflex.commons.logging.Log4jLogger;

/**
 *
 * @author EKovtunenko
 */
public class RegisterRedmineUserCommand implements Command {

    @Inject
    private UserRepository users;

    @Override
    public List<TGMessage> invoke(Message message) {
        List<TGMessage> ret = new ArrayList<>();
        TGMessage msg = new TGMessage(MessageType.TEXT);
        String[] split = message.getText().split(" ");
        String text = "Пользователь успешно зарегистрирован";
        try {
            if (users.isRedmineUserRegistered(split[1])) {
                text = "Пользователь уже зарегистрирован";
            } else {
                users.updateRedmineId(message.getFrom().getId(), split[1]);
            }
            msg.setText(text);
        } catch (SQLException ex) {
            Log4jLogger.getLogger(this.getClass()).error(ex);
            msg.setText(ex.getMessage());
        }
        ret.add(msg);
        return ret;
    }

}
