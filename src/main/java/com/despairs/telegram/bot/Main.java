/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot;

import com.despairs.telegram.bot.schedule.Schedule;
import com.despairs.telegram.bot.schedule.ScheduleRegistry;
import com.despairs.telegram.bot.utils.FileUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 *
 * @author EKovtunenko
 */
public class Main {

    public static void main(String[] args) throws SQLException {
        try {
            ApiContextInitializer.init();
            TelegramBotsApi botsApi = new TelegramBotsApi();
            Bot bot = new Bot();
            botsApi.registerBot(bot);
            Schedule schedule = new Schedule(bot);
            ScheduleRegistry.getInstance().add(schedule);
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(schedule, 0, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
