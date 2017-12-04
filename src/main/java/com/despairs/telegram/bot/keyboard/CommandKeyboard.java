/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.keyboard;

import com.despairs.telegram.bot.commands.Command;
import com.despairs.telegram.bot.commands.Scope;
import com.despairs.telegram.bot.commands.ScopeType;
import com.despairs.telegram.bot.commands.Visible;
import com.despairs.telegram.bot.commands.registry.CommandRegistry;
import com.despairs.telegram.bot.db.repo.UserRepository;
import com.despairs.telegram.bot.db.repo.impl.UserRepositoryImpl;
import com.despairs.telegram.bot.model.User;
import com.despairs.telegram.bot.utils.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.Map;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

/**
 *
 * @author EKovtunenko
 */
public class CommandKeyboard extends ReplyKeyboardMarkup {

    private final UserRepository users = UserRepositoryImpl.getInstance();

    private static final int maxColumnCount = 4;
    private final User user;

    public CommandKeyboard(User user) throws SQLException {
        super();
        this.user = user;
        setSelective(Boolean.TRUE);
        setResizeKeyboard(Boolean.TRUE);
        buildKeyboard();

    }

    private void buildKeyboard() {
        Map<String, Command> commands = CommandRegistry.getInstance().getCommands();
        int columnCount = 0;
        KeyboardRow row = null;
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            Command command = entry.getValue();
            if (isVisible(command) && isAllowed(command)) {
                if (row == null || columnCount % maxColumnCount == 0) {
                    row = new KeyboardRow();
                    getKeyboard().add(row);
                }
                row.add(new KeyboardButton(entry.getKey()));
                columnCount++;
            }
        }
    }

    private boolean isVisible(Command command) {
        Annotation visible = AnnotationUtils.findAnnotation(command.getClass(), Visible.class);
        return visible != null;
    }

    private boolean isAllowed(Command command) {
        boolean ret = Boolean.TRUE;
        Annotation scope = AnnotationUtils.findAnnotation(command.getClass(), Scope.class);
        if (scope != null) {
            Scope _scope = (Scope) scope;
            if (_scope.type().equals(ScopeType.ADMIN)) {
                if (user == null || !user.isAdmin()) {
                    ret = Boolean.FALSE;
                }
            }
        }
        return ret;
    }
}
