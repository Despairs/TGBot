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
public class RedmineIssueKeyboard extends InlineKeyboardMarkup {

    private final Integer issueId;

    public RedmineIssueKeyboard(Integer issueId) {
        super();
        this.issueId = issueId;
        buildKeyboard();
    }

    private void buildKeyboard() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Взять в работу");
        button.setCallbackData("assign_redmine_issue_" + issueId);
        setKeyboard(Arrays.asList(Arrays.asList(button)));
    }
}
