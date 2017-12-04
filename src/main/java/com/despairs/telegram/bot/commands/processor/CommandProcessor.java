/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.processor;

import com.despairs.telegram.bot.TGMessageSender;
import com.despairs.telegram.bot.model.User;

/**
 *
 * @author EKovtunenko
 */
public interface CommandProcessor {

    void process();

    CommandProcessor bindSender(TGMessageSender sender);
    
    CommandProcessor bindUser(User user);
}
