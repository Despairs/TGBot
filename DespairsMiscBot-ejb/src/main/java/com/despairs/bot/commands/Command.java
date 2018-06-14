/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.commands;

import java.util.List;
import javax.enterprise.context.RequestScoped;

import com.despairs.bot.model.TGMessage;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
@RequestScoped
public interface Command {

    List<TGMessage> invoke(Message message);
}
