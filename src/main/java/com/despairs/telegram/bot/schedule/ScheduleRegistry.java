/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.schedule;

import com.despairs.telegram.bot.commands.impl.BurgerKingCommand;
import com.despairs.telegram.bot.commands.Command;
import com.despairs.telegram.bot.commands.impl.KfcCommand;
import com.despairs.telegram.bot.commands.impl.StartCommand;
import com.despairs.telegram.bot.commands.impl.UnknownCommand;
import com.despairs.telegram.bot.commands.registry.CommandRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author EKovtunenko
 */
public class ScheduleRegistry {

    private static final List<Runnable> schedules = new ArrayList<>();

    private static final ScheduleRegistry instance = new ScheduleRegistry();

    public static ScheduleRegistry getInstance() {
        return instance;
    }

    public void runAll() {
        schedules.parallelStream().forEach(s -> s.run());
    }

    public void add(Runnable schedule) {
        schedules.add(schedule);
    }

}
