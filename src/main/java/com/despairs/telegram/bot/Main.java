/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot;

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

    private static final String TARGET_CHANNEL = "@despairstestchannel";
    private static final String TOKEN = "314335252:AAGe1_MiXdMl5NwK3OQnrAvy6G08U1qENfA";

    public static void main(String[] args) {

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();
        Bot bot;
        try {
            bot = new Bot(TOKEN, TARGET_CHANNEL);
            botsApi.registerBot(bot);
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(bot, 0, 10, TimeUnit.MINUTES);;
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
