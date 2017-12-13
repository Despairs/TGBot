/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.utils;

import com.despairs.bot.commands.Command;
import com.despairs.bot.commands.CommandCfg;
import com.despairs.bot.commands.ScopeType;
import com.despairs.bot.model.User;

/**
 *
 * @author EKovtunenko
 */
public class CommandHelper {

    public static CommandCfg getCfg(Command command) {
        return (CommandCfg) AnnotationUtils.findAnnotation(command.getClass(), CommandCfg.class);
    }

    public static Boolean isVisible(Command command) {
        boolean ret = Boolean.FALSE;
        CommandCfg commandCfg = CommandHelper.getCfg(command);
        if (commandCfg != null) {
            ret = commandCfg.visible();
        }
        return ret;
    }

    public static String getAlias(Command command) {
        String ret = null;
        CommandCfg commandCfg = CommandHelper.getCfg(command);
        if (commandCfg != null) {
            ret = "####default".equals(commandCfg.alias()) ? null : commandCfg.alias();
        }
        return ret;
    }

    public static Boolean isAllowedForUser(Command command, User user) {
        boolean ret = Boolean.TRUE;
        CommandCfg commandCfg = getCfg(command);
        if (commandCfg != null) {
            if (commandCfg.scope().equals(ScopeType.ADMIN)) {
                if (user == null || !user.isAdmin()) {
                    ret = Boolean.FALSE;
                }
            }
        }
        return ret;
    }
}
