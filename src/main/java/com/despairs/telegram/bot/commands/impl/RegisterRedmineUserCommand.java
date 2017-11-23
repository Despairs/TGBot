/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.impl;

import com.despairs.telegram.bot.commands.Command;
import com.despairs.telegram.bot.db.repo.UserRepository;
import com.despairs.telegram.bot.db.repo.impl.UserRepositoryImpl;
import com.despairs.telegram.bot.model.MessageType;
import com.despairs.telegram.bot.model.TGMessage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
public class RegisterRedmineUserCommand implements Command {

    private final UserRepository users = UserRepositoryImpl.getInstance();

    @Override
    public List<TGMessage> invoke(Message message) {
        List<TGMessage> ret = new ArrayList<>();
        TGMessage msg = new TGMessage(MessageType.TEXT);
        String[] split = message.getText().split(" ");
        String text = "Пользователь успешно зарегистрирован";
        try {
            if (users.isUserRegistered(split[1])) {
                text = "Пользователь уже зарегистрирован";
            } else {
                users.registerUser(String.valueOf(message.getFrom().getId()), message.getFrom().getUserName(), split[1]);
            }
            msg.setText(text);
        } catch (SQLException ex) {
            ex.printStackTrace();
            msg.setText(ex.getMessage());
        }
        ret.add(msg);
        return ret;
    }

}
