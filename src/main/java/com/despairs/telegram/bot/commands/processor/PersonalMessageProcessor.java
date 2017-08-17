/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.processor;

import org.telegram.telegrambots.api.objects.Update;

/**
 *
 * @author EKovtunenko
 */
public class PersonalMessageProcessor extends BaseProcessor {

    public PersonalMessageProcessor(Update update) {
        super(update.getMessage());
    }

}
