/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.impl;

import com.despairs.telegram.bot.commands.Command;
import com.despairs.telegram.bot.commands.Scope;
import com.despairs.telegram.bot.commands.ScopeType;
import com.despairs.telegram.bot.commands.Visible;
import com.despairs.telegram.bot.keyboard.JobKeyboard;
import com.despairs.telegram.bot.keyboard.SalaryKeyboard;
import com.despairs.telegram.bot.model.MessageType;
import com.despairs.telegram.bot.model.ParseMode;
import com.despairs.telegram.bot.model.TGMessage;
import com.despairs.telegram.bot.model.User;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
@Visible
public class JobStartCommand implements Command {

    private final User user;

    public JobStartCommand(User user) {
        this.user = user;
    }

    @Override
    public List<TGMessage> invoke(Message message) {
        List<TGMessage> ret = new ArrayList<>();
        TGMessage msg = new TGMessage(MessageType.TEXT);
        msg.setParseMode(ParseMode.HTML);
        ret.add(msg);
        msg.setText("Time Management Menu");
        msg.setKeyboard(new JobKeyboard(SalaryKeyboard.MAIN, user));
        return ret;
    }

}
