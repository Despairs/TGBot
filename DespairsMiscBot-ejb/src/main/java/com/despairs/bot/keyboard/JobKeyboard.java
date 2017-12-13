/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.keyboard;

import com.despairs.bot.db.repo.JobRepository;
import com.despairs.bot.db.repo.impl.JobRepositoryImpl;
import com.despairs.bot.model.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.iflex.commons.logging.Log4jLogger;

/**
 *
 * @author EKovtunenko
 */
public class JobKeyboard extends InlineKeyboardMarkup {

    public static final int MAIN = 0;
    public static final int INPUT = 1;
    public static final int DELETE = 2;
    public static final int VIEW = 3;
    public static final int VIEW_ALL = 4;
    public static final int VIEW_AGGREGATION = 5;
    public static final int VIEW_ALL_LAST_PAYMENT = 6;
    public static final int VIEW_AGGREGATION_LAST_PAYMENT = 7;
    public static final int VIEW_ALL_ALL_TIME = 8;
    public static final int VIEW_AGGREGATION_ALL_TIME = 9;
//    public static final int SET_PAYMENT_DATE = 6;
    public static final int HIDE = -1;

    private final int level;

    private final List<InlineKeyboardButton> hide = Arrays.asList(new InlineKeyboardButton("Скрыть")
            .setCallbackData("JOB#HIDE"));
    private final List<InlineKeyboardButton> reset = Arrays.asList(new InlineKeyboardButton("На главную")
            .setCallbackData("JOB#RESET"));

    private final User user;
    private final JobRepository jobs = JobRepositoryImpl.getInstance();

    public JobKeyboard(int level, User user) {
        super();
        this.user = user;
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
                keyboard = buildProjectButtons("INPUT", "HOURS#[DATE{yyyy-MM-dd}]#[COMMENT]");
                break;
            case VIEW:
                keyboard = buildViewKeyboard();
                break;
            case VIEW_ALL:
                keyboard = buildViewPaymentKeyboard("VIEW#ALL");
                break;
            case VIEW_AGGREGATION:
                keyboard = buildViewPaymentKeyboard("VIEW#AGGREGATION");
                break;
            case VIEW_AGGREGATION_ALL_TIME:
                keyboard = buildProjectButtons("VIEW#AGGREGATION#ALL_TIME");
                break;
            case VIEW_AGGREGATION_LAST_PAYMENT:
                keyboard = buildProjectButtons("VIEW#AGGREGATION#LAST_PAYMENT");
                break;
            case VIEW_ALL_ALL_TIME:
                keyboard = buildProjectButtons("VIEW#ALL#ALL_TIME");
                break;
            case VIEW_ALL_LAST_PAYMENT:
                keyboard = buildProjectButtons("VIEW#ALL#LAST_PAYMENT");
                break;
        }
        setKeyboard(keyboard);
    }

    private List<List<InlineKeyboardButton>> buildMainKeyboard() {
        return Arrays.asList(
                Arrays.asList(new InlineKeyboardButton("Ввод")
                        .setCallbackData("JOB#INPUT")),
                Arrays.asList(new InlineKeyboardButton("Изменение")
                        .setSwitchInlineQueryCurrentChat("JOB#UPDATE#ID#HOURS#[DATE{yyyy-MM-dd}]#[COMMENT]")),
                Arrays.asList(new InlineKeyboardButton("Удаление")
                        .setSwitchInlineQueryCurrentChat("JOB#DELETE#ID")),
                Arrays.asList(new InlineKeyboardButton("Просмотр")
                        .setCallbackData("JOB#VIEW")),
                hide);
    }

    private List<List<InlineKeyboardButton>> buildViewKeyboard() {
        return Arrays.asList(
                Arrays.asList(new InlineKeyboardButton("Все вхождения")
                        .setCallbackData("JOB#VIEW#ALL")),
                Arrays.asList(new InlineKeyboardButton("Аггрегация")
                        .setCallbackData("JOB#VIEW#AGGREGATION")),
                reset);
    }

    private List<List<InlineKeyboardButton>> buildViewPaymentKeyboard(String action) {
        return Arrays.asList(
                Arrays.asList(new InlineKeyboardButton("С последней выплаты")
                        .setCallbackData("JOB#" + action + "#LAST_PAYMENT")),
                Arrays.asList(new InlineKeyboardButton("За все время")
                        .setCallbackData("JOB#" + action + "#ALL_TIME")),
                reset);
    }

    private List<List<InlineKeyboardButton>> buildProjectButtons(String action) {
        return buildProjectButtons(action, "");
    }

    private List<List<InlineKeyboardButton>> buildProjectButtons(String action, String suffix) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        try {
            List<String> availableProjects = jobs.getProjects(user.getId());
            availableProjects.forEach((project) -> {
                keyboard.add(Arrays.asList(new InlineKeyboardButton(project)
                        .setSwitchInlineQueryCurrentChat("JOB#" + action + "#" + project + "#" + suffix)));
            });
        } catch (SQLException ex) {
            Log4jLogger.getLogger(this.getClass()).error(ex);
        }
        keyboard.add(reset);
        return keyboard;
    }

}
