/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.commands.processor;

import com.despairs.bot.model.User;
import javax.enterprise.context.RequestScoped;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
@RequestScoped
public interface CommandProcessor {

    void process();

    CommandProcessor bindMessage(Message message);

    CommandProcessor bindUser(User user);
}
