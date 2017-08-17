/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.registry;

import com.despairs.telegram.bot.commands.Command;
import com.despairs.telegram.bot.commands.impl.StartCommand;
import com.despairs.telegram.bot.commands.impl.UnknownCommand;
import java.util.HashMap;
import java.util.Map;

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
        Command command = commands.get(name);
        if (command == null) {
            command = unknownCommand;
        }
        return command;
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

}
