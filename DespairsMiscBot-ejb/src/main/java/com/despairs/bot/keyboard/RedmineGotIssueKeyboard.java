/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.keyboard;

import java.util.Arrays;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 *
 * @author EKovtunenko
 */
public class RedmineGotIssueKeyboard extends InlineKeyboardMarkup {

    private final String username;

    public RedmineGotIssueKeyboard(String username) {
        super();
        this.username = username;
        buildKeyboard();
    }

    private void buildKeyboard() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Назначено " + username);
        button.setCallbackData("redmine_issue_already_assigned");
        setKeyboard(Arrays.asList(Arrays.asList(button)));
    }
}
