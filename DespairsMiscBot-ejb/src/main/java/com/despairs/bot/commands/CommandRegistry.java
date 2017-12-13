/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.commands;

import com.despairs.bot.Bot;
import com.despairs.bot.commands.impl.UnknownCommand;
import com.despairs.bot.utils.CommandHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 * @author EKovtunenko
 */
@Singleton
public class CommandRegistry {

    @Inject
    private Instance<Command> commands;

    private static final Map<String, Command> commandWithAliases = new HashMap<>();

    @Inject
    private UnknownCommand unknownCommand;

    @PostConstruct
    public void init() {
        for (Command command : commands) {
            String alias = CommandHelper.getAlias(command);
            if (alias != null) {
                commandWithAliases.put(alias, command);
            }
        }
    }

    public Command getCommand(String name) {
        Command ret = unknownCommand;
        if (name.contains(Bot.BOT_USER_NAME)) {
            name = name.replace(Bot.BOT_USER_NAME, "");
        }
        ret = commandWithAliases.get(name);
        if (ret == null) {
            final String _name = name;
            List<String> foundedAliases = commandWithAliases.keySet().stream().filter(commandAlias -> _name.startsWith(commandAlias)).collect(Collectors.toList());
            if (foundedAliases.size() == 1) {
                String alias = foundedAliases.get(0);
                ret = commandWithAliases.get(alias);
            } else {
                ret = unknownCommand;
            }
        }
        return ret;
    }

    public static Map<String, Command> getCommands() {
        return commandWithAliases;
    }

}
