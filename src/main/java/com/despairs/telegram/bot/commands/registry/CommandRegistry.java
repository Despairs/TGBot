/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.registry;

import com.despairs.telegram.bot.Bot;
import com.despairs.telegram.bot.commands.Command;
import com.despairs.telegram.bot.commands.impl.StartCommand;
import com.despairs.telegram.bot.commands.impl.UnknownCommand;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author EKovtunenko
 */
public class CommandRegistry {

    private static final Map<String, Command> commands = new HashMap<>();
    private final UnknownCommand unknownCommand = new UnknownCommand();

    private static final CommandRegistry instance = new CommandRegistry();

    private CommandRegistry() {
        commands.put("/start", new StartCommand());
    }

    public static CommandRegistry getInstance() {
        return instance;
    }

    public void registerCommand(String name, Command command) {
        commands.put(name, command);
    }

    public Command getCommand(String name) {
        if (name.contains(Bot.BOT_USER_NAME)) {
            name = name.replace(Bot.BOT_USER_NAME, "");
        }
        Command command = commands.get(name);
        if (command == null) {
            final String _name = name;
            List<String> foundedAliases = commands.keySet().stream().filter(commandAlias -> _name.startsWith(commandAlias)).collect(Collectors.toList());
            if (foundedAliases.size() == 1) {
                String alias = foundedAliases.get(0);
                command = commands.get(alias);
            } else {
                command = unknownCommand;
            }
        }
        return command;
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

}
