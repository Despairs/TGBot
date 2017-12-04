/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.keyboard;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 *
 * @author EKovtunenko
 */
public class SalaryKeyboard extends InlineKeyboardMarkup {

    private static final String PERIOD_PATTERN = "LLLL yyyy";

    public static final int MAIN = 0;
    public static final int INPUT = 1;
    public static final int DELETE = 2;
    public static final int VIEW = 3;
    public static final int VIEW_ALL = 4;
    public static final int VIEW_AGGREGATION = 5;
    public static final int HIDE = 8;

    private final int level;

    private final List<InlineKeyboardButton> hide = Arrays.asList(new InlineKeyboardButton("Скрыть")
            .setCallbackData("SALARY#HIDE"));
    private final List<InlineKeyboardButton> reset = Arrays.asList(new InlineKeyboardButton("На главную")
            .setCallbackData("SALARY#RESET"));

    public SalaryKeyboard(int level) {
        super();
        this.level = level;
        buildKeyboard();
    }

    private void buildKeyboard() {
        List<List<InlineKeyboardButton>> keyboard = null;
        switch (level) {
            case MAIN:
                keyboard = buildMainKeyboard();
                break;
            case INPUT:
                keyboard = buildPeriodButtons("INPUT");
                break;
            case VIEW:
                keyboard = buildViewKeyboard();
                break;
            case VIEW_ALL:
                keyboard = buildPeriodButtons("VIEW#ALL");
                break;
            case VIEW_AGGREGATION:
                keyboard = buildPeriodButtons("VIEW#AGGREGATION");
                break;
        }
        setKeyboard(keyboard);
    }

    private List<List<InlineKeyboardButton>> buildMainKeyboard() {
        return Arrays.asList(
                Arrays.asList(new InlineKeyboardButton("Ввод")
                        .setCallbackData("SALARY#INPUT")),
                Arrays.asList(new InlineKeyboardButton("Удаление")
                        .setSwitchInlineQueryCurrentChat("SALARY#DELETE#")),
                Arrays.asList(new InlineKeyboardButton("Просмотр")
                        .setCallbackData("SALARY#VIEW")),
                hide);
    }

    private List<List<InlineKeyboardButton>> buildViewKeyboard() {
        return Arrays.asList(
                Arrays.asList(new InlineKeyboardButton("Все вхождения")
                        .setCallbackData("SALARY#VIEW#ALL")),
                Arrays.asList(new InlineKeyboardButton("Аггрегация")
                        .setCallbackData("SALARY#VIEW#AGGREGATION")),
                reset);
    }

    private List<List<InlineKeyboardButton>> buildPeriodButtons(String action) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<String> availablePeriods = getAvailablePeriods();
        availablePeriods.forEach((period) -> {
            keyboard.add(Arrays.asList(new InlineKeyboardButton(period)
                    .setSwitchInlineQueryCurrentChat("SALARY#" + action + "#" + period + "#")));
        });
        keyboard.add(reset);
        return keyboard;
    }

    private List<String> getAvailablePeriods() {
        List<String> ret = new ArrayList();
        for (int i = -1; i < 1; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, i);
            ret.add(toString(calendar));
        }
        return ret;
    }

    private String toString(Calendar cal) {
        return new SimpleDateFormat(PERIOD_PATTERN, new Locale("ru")).format(cal.getTime());
    }
}
