/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.keyboard;

import com.despairs.bot.commands.Command;
import com.despairs.bot.commands.CommandRegistry;
import com.despairs.bot.model.User;
import com.despairs.bot.utils.CommandHelper;
import java.util.Map;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

/**
 *
 * @author EKovtunenko
 */
public class CommandKeyboard extends ReplyKeyboardMarkup {

    public static final int maxColumnCount = 4;

    private final User user;

    public CommandKeyboard(User user) {
        this.user = user;
        setSelective(Boolean.TRUE);
        setResizeKeyboard(Boolean.TRUE);
        buildKeyboard();

    }

    private void buildKeyboard() {
        Map<String, Command> commands = CommandRegistry.getCommands();
        int columnCount = 0;
        KeyboardRow row = null;
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            Command command = entry.getValue();
            if (CommandHelper.isVisible(command) && CommandHelper.isAllowedForUser(command, user)) {
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
