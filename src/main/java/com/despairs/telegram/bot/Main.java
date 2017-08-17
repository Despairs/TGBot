/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot;

import com.despairs.telegram.bot.schedule.Schedule;
import com.despairs.telegram.bot.schedule.ScheduleRegistry;
import com.despairs.telegram.bot.utils.FileUtils;
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

    private static final String CFG = "bot.cfg";

    public static void main(String[] args) {

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();
        Bot bot;

        List<String> cfg = FileUtils.readAsList(CFG);
        try {
            String token = cfg.get(0);
            bot = new Bot(token);
            botsApi.registerBot(bot);
            String channelId = cfg.get(1);
            Schedule schedule = new Schedule(bot, channelId);
            ScheduleRegistry.getInstance().add(schedule);
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(schedule, 0, 10, TimeUnit.MINUTES);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
