/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.commands.impl;

import com.despairs.bot.commands.Command;
import com.despairs.bot.commands.CommandCfg;
import com.despairs.bot.commands.ScopeType;
import com.despairs.bot.keyboard.SalaryKeyboard;
import com.despairs.bot.model.MessageType;
import com.despairs.bot.model.ParseMode;
import com.despairs.bot.model.TGMessage;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
@CommandCfg(alias="ЗП", scope = ScopeType.ADMIN, visible = true)
public class SalaryStartCommand implements Command {

    @Override
    public List<TGMessage> invoke(Message message) {
        List<TGMessage> ret = new ArrayList<>();
        TGMessage msg = new TGMessage(MessageType.TEXT);
        msg.setParseMode(ParseMode.HTML);
        ret.add(msg);
        msg.setText("Salary Project Menu");
        msg.setKeyboard(new SalaryKeyboard(SalaryKeyboard.MAIN));
        return ret;
    }

}
