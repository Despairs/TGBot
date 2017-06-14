/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot;

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
            bot = new Bot(cfg.get(0), cfg.get(1));
            botsApi.registerBot(bot);
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(bot, 0, 10, TimeUnit.MINUTES);;
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
