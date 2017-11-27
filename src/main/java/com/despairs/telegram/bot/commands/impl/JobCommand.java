/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.impl;

import com.despairs.telegram.bot.commands.Command;
import com.despairs.telegram.bot.db.repo.JobRepository;
import com.despairs.telegram.bot.db.repo.UserRepository;
import com.despairs.telegram.bot.db.repo.impl.JobRepositoryImpl;
import com.despairs.telegram.bot.db.repo.impl.UserRepositoryImpl;
import com.despairs.telegram.bot.model.JobEntry;
import com.despairs.telegram.bot.model.MessageType;
import com.despairs.telegram.bot.model.ParseMode;
import com.despairs.telegram.bot.model.TGMessage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
public class JobCommand implements Command {

    private static final String USAGE_TEXT = "<b>Использование:</b> <pre>/job %command% %project% %time_entry%</pre>\n\n"
            + "<b>Доступные команды:</b>\n"
            + "Фиксация времени по проекту: <pre>/job put %project% %time_entry% [%date%]</pre>\n\n"
            + "Список списаний времени по проекту: <pre>/job get [%project%]</pre>\n\n"
            + "Суммарная статистика списаний времени по проекту: <pre>/job getStat [%project%]</pre>\n\n"
            + "Суммарная статистика списаний времени по проекту с группировкой по дням: <pre>/job getStatByDay [%project%]</pre>\n\n"
            + "<b>[%project%]</b> - опциональный параметр. Без указания будет выведена информация по всем проектам."
            + "<b>[%date%]</b> - опциональный параметр. Без указания будет использоваться SYSDATE";
    private static final String UNKNOWN_COMMAND = "Неизвестная команда: %s";
    private static final String ENTRY_PUTTED = "%s добавил %.2f ч. в проект %s";
    private static final String ENTRY_PUTTED_WITH_DATE = "%s добавил %.2f ч. в проект %s за %s";

    private static final String ENTRIES_TITLE_MESSAGES = "<b>Проект</b>: <pre>%s</pre>\n";
    private static final String ENTRIES_TABLE_HEADER_MESSAGES = "<b>Дата</b>\t|\t<b>Часы</b>\n";
    private static final String ENTRIES_TABLE_ROW = "<pre>%s\t|\t%.2f</pre>";
    private static final String ENTRIES_TABLE_ROW_TOTAL = "<b>Часов</b>: <pre>%s</pre>\n";

    private final UserRepository users = UserRepositoryImpl.getInstance();
    private final JobRepository jobs = JobRepositoryImpl.getInstance();

    @Override
    public List<TGMessage> invoke(Message message) {
        List<TGMessage> ret = new ArrayList<>();
        TGMessage msg = new TGMessage(MessageType.TEXT);
        msg.setParseMode(ParseMode.HTML);
        ret.add(msg);

        String[] splittedCommand = message.getText().split(" ");

        // Валидация аргументов
        if (splittedCommand.length < 2 || splittedCommand.length > 5) {
            msg.setText(USAGE_TEXT);
            return ret;
        }

        try {
            Integer userId = message.getFrom().getId();
            String name = message.getFrom().getUserName();
            // Регистрируем юзера 
            if (!users.isUserRegistered(userId)) {
                users.registerUser(userId, name);
            }

            String messageText = null;
            String command = splittedCommand[1];
            String project = splittedCommand.length > 2 ? splittedCommand[2] : null;
            switch (command) {
                case "put":
                    Double timeEntry = Double.parseDouble(splittedCommand[3]);
                    String timestamp = splittedCommand.length > 4 ? splittedCommand[4] : null;
                    if (timestamp == null) {
                        jobs.createEntry(userId, project, timeEntry);
                        messageText = String.format(ENTRY_PUTTED, name, timeEntry, project);
                    } else {
                        jobs.createEntry(userId, project, timeEntry, new SimpleDateFormat(JobEntry.DATE_PATTERN).parse(timestamp));
                        messageText = String.format(ENTRY_PUTTED_WITH_DATE, name, timeEntry, project, timestamp);
                    }                   
                    break;
                case "get": {
                    List<JobEntry> entries = project == null ? jobs.getEntries(userId) : jobs.getEntries(userId, project);
                    messageText = buildEntriesMessage(entries);
                    break;
                }
                case "getStat": {
                    List<JobEntry> entries = project == null ? jobs.getEntries(userId) : jobs.getEntries(userId, project);
                    messageText = buildStatisticMessage(entries);
                    break;
                }
                case "getStatByDay": {
                    List<JobEntry> entries = project == null ? jobs.getEntries(userId) : jobs.getEntries(userId, project);
                    messageText = buildStatisticByDayMessage(entries);
                    break;
                }
                default:
                    messageText = String.format(UNKNOWN_COMMAND, command);
                    break;
            }
            msg.setText(messageText.isEmpty() ? "Записей не найдено" : messageText);
        } catch (Exception ex) {
            ex.printStackTrace();
            msg.setText(ex.getMessage());
        }
        return ret;
    }

    private String buildStatisticByDayMessage(List<JobEntry> entries) {
        StringBuilder sb = new StringBuilder();
        entries.stream().map(entry -> entry.getProject()).distinct().forEach((String project) -> {
            sb.append(String.format(ENTRIES_TITLE_MESSAGES, project));
            sb.append(ENTRIES_TABLE_HEADER_MESSAGES);
            Map<String, Double> data = entries.stream()
                    .filter(entry -> entry.getProject().equals(project))
                    .collect(Collectors.groupingBy(JobEntry::getDateAsString, Collectors.summingDouble(JobEntry::getDuration)));
            data.entrySet().forEach((entry) -> {
                sb.append(String.format(ENTRIES_TABLE_ROW, entry.getKey(), entry.getValue()));
                sb.append("\n");
            });
            sb.append("\n");
        });
        return sb.toString();
    }

    private String buildStatisticMessage(List<JobEntry> entries) {
        StringBuilder sb = new StringBuilder();
        entries.stream().map(entry -> entry.getProject()).distinct().forEach((String project) -> {
            sb.append(String.format(ENTRIES_TITLE_MESSAGES, project));
            Double totalDuration = entries.stream()
                    .filter(entry -> entry.getProject().equals(project))
                    .collect(Collectors.summingDouble(JobEntry::getDuration));
            sb.append(String.format(ENTRIES_TABLE_ROW_TOTAL, totalDuration));
            sb.append("\n");
        });
        return sb.toString();
    }

    private String buildEntriesMessage(List<JobEntry> entries) {
        StringBuilder sb = new StringBuilder();
        entries.stream().map(entry -> entry.getProject()).distinct().forEach(project -> {
            sb.append(String.format(ENTRIES_TITLE_MESSAGES, project));
            sb.append(ENTRIES_TABLE_HEADER_MESSAGES);
            entries.stream().filter(entry -> entry.getProject().equals(project)).forEach(entry -> {
                sb.append(String.format(ENTRIES_TABLE_ROW, entry.getDateAsString(), entry.getDuration()));
                sb.append("\n");
            });
        });
        return sb.toString();
    }

}
