/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.schedule;

import java.util.ArrayList;
import java.util.List;

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
        schedules.parallelStream().forEach(Runnable::run);
    }

    public void add(Runnable schedule) {
        schedules.add(schedule);
    }

}
