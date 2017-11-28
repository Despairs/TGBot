/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.keyboard;

import com.despairs.telegram.bot.model.JobEntry;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 *
 * @author EKovtunenko
 */
public class JobEditEntryKeyboard extends InlineKeyboardMarkup {

    private static final String COMMAND_PATTERN = "/job edit %d -H%.2f -D%s -C\"%s\"";
    
    private static final int maxColumnCount = 8;

    public JobEditEntryKeyboard(List<JobEntry> entries) {
        super();
        buildKeyboard(entries);

    }

    private void buildKeyboard(List<JobEntry> entries) {
        int columnCount = 0;
        List<InlineKeyboardButton> row = null;
        for (JobEntry entry : entries) {
            if (row == null || columnCount % maxColumnCount == 0) {
                row = new ArrayList<>();
                getKeyboard().add(row);
            }
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(entry.getId().toString());
            button.setSwitchInlineQueryCurrentChat(String.format(COMMAND_PATTERN, entry.getId(), entry.getDuration(), entry.getDateAsString(), entry.getComment()));           
            columnCount++;
            row.add(button);
        }
    }

}
