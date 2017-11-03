/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.keyboard;

import com.despairs.telegram.bot.commands.Command;
import com.despairs.telegram.bot.commands.registry.CommandRegistry;
import com.despairs.telegram.bot.commands.VisibleCommand;
import java.util.Map;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

/**
 *
 * @author EKovtunenko
 */
public class CommandKeyboard extends ReplyKeyboardMarkup {

    private static final int maxColumnCount = 2;

    public CommandKeyboard() {
        super();
        setSelective(Boolean.TRUE);
        setResizeKeyboard(Boolean.TRUE);
        buildKeyboard();

    }

    private void buildKeyboard() {
        Map<String, Command> commands = CommandRegistry.getInstance().getCommands();
        int columnCount = 0;
        KeyboardRow row = null;
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            if (entry.getValue() instanceof VisibleCommand) {
                if (row == null || columnCount % maxColumnCount == 0) {
                    row = new KeyboardRow();
                    getKeyboard().add(row);
                }
                row.add(new KeyboardButton(entry.getKey()));
                columnCount++;
            }
        }
    }
}
