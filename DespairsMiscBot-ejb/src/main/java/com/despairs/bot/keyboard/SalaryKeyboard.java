/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.keyboard;

import com.despairs.bot.utils.DateUtils;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

/**
 * @author EKovtunenko
 */
public class SalaryKeyboard extends InlineKeyboardMarkup {

    public static final int MAIN = 0;
    public static final int INPUT = 1;
    public static final int VIEW = 3;
    public static final int VIEW_ALL = 4;
    public static final int VIEW_AGGREGATION = 5;
    private static final int DEFAULT_DEEP = -1;
    private final int level;

    private final List<InlineKeyboardButton> hide = Collections.singletonList(new InlineKeyboardButton("Скрыть")
            .setCallbackData("SALARY#HIDE"));
    private final List<InlineKeyboardButton> reset = Collections.singletonList(new InlineKeyboardButton("На главную")
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
                keyboard = buildPeriodButtons("VIEW#ALL", -6);
                break;
            case VIEW_AGGREGATION:
                keyboard = buildPeriodButtons("VIEW#AGGREGATION", -6);
                break;
        }
        setKeyboard(keyboard);
    }

    private List<List<InlineKeyboardButton>> buildMainKeyboard() {
        return Arrays.asList(
                Collections.singletonList(new InlineKeyboardButton("Ввод")
                        .setCallbackData("SALARY#INPUT")),
                Collections.singletonList(new InlineKeyboardButton("Удаление")
                        .setSwitchInlineQueryCurrentChat("SALARY#DELETE#")),
                Collections.singletonList(new InlineKeyboardButton("Просмотр")
                        .setCallbackData("SALARY#VIEW")),
                hide);
    }

    private List<List<InlineKeyboardButton>> buildViewKeyboard() {
        return Arrays.asList(
                Collections.singletonList(new InlineKeyboardButton("Все вхождения")
                        .setCallbackData("SALARY#VIEW#ALL")),
                Collections.singletonList(new InlineKeyboardButton("Аггрегация")
                        .setCallbackData("SALARY#VIEW#AGGREGATION")),
                Collections.singletonList(new InlineKeyboardButton("Аггрегация за все время")
                        .setSwitchInlineQueryCurrentChat("SALARY#VIEW#ALL_TIME_AGGREGATION")),
                reset);
    }

    private List<List<InlineKeyboardButton>> buildPeriodButtons(String action) {
        return buildPeriodButtons(action, DEFAULT_DEEP);
    }

    private List<List<InlineKeyboardButton>> buildPeriodButtons(String action, int deep) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<String> availablePeriods = getAvailablePeriods(deep);
        availablePeriods.forEach((period) -> {
            keyboard.add(Collections.singletonList(new InlineKeyboardButton(period)
                    .setSwitchInlineQueryCurrentChat("SALARY#" + action + "#" + period + "#")));
        });
        keyboard.add(reset);
        return keyboard;
    }

    private List<String> getAvailablePeriods(int deep) {
        List<String> ret = new ArrayList<>();
        for (int i = deep; i < 1; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, i);
            ret.add(DateUtils.toPeriod(calendar));
        }
        return ret;
    }

}
