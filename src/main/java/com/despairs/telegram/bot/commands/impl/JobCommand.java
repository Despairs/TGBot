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
import com.despairs.telegram.bot.keyboard.JobEditEntryKeyboard;
import com.despairs.telegram.bot.model.JobEntry;
import com.despairs.telegram.bot.model.MessageType;
import com.despairs.telegram.bot.model.ParseMode;
import com.despairs.telegram.bot.model.TGMessage;
import com.despairs.telegram.bot.utils.StringUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
            + "Фиксация времени по проекту: <pre>/job put %project% %time_entry% [-D%date%] [-C\"%ANY FREE FORMATTED DATA%\"]</pre>\n\n"
            + "Изменение записи: <pre>/job edit %id% [-H%hours%] [-D%date%] [-C\"%ANY FREE FORMATTED DATA%\"]</pre>\n\n"
            + "Удаление записи: <pre>/job remove %id%</pre>\n\n"
            + "Список списаний времени по проекту: <pre>/job get [-ID%id%] [%project%]</pre>\n\n"
            + "Суммарная статистика списаний времени по проекту: <pre>/job getStat [%project%]</pre>\n\n"
            + "Суммарная статистика списаний времени по проекту с группировкой по дням: <pre>/job getStatByDay [%project%]</pre>\n\n"
            + "<b>[%project%]</b> - опциональный параметр. Без указания будет выведена информация по всем проектам\n"
            + "<b>[-ID%id%]</b> - опциональный параметр. При указании поиск будет осуществляться только по заданному ID\n"
            + "<b>[-H%hours%]</b> - опциональный параметр. Используется только для изменения записи\n"
            + "<b>[-D%date%]</b> - опциональный параметр. Без указания будет использоваться SYSDATE. <b>Формат даты: yyyy-MM-dd</b>\n"
            + "<b>[-C\"%comment%\"]</b> - опциональный параметр. Обязательно использование кавычек\n";

    private static final String UNKNOWN_COMMAND = "Неизвестная команда: %s";

    private static final String ENTRY_PUTTED = "<b>%s</b> добавил %.2f ч. в проект %s. <b>ID</b> = %d";
    private static final String ENTRY_PUTTED_WITH_DATE = "<b>%s</b> добавил %.2f ч. в проект %s за %s. <b>ID</b> = %d";
    private static final String ENTRY_DELETED = "<b>%s</b> удалил запись с <b>ID</b> = %d";
    private static final String ENTRY_UPDATED = "<b>%s</b> обновил запись с <b>ID</b> = %d";

    private static final String ENTRY_NOT_FOUND = "Запись с <b>ID</b> = %d не найдена";

    private static final String ENTRIES_TITLE_MESSAGES = "<b>Проект</b>: <pre>%s</pre>\n";
    private static final String ENTRIES_TABLE_HEADER_MESSAGES = "<b>Дата</b>\t|\t<b>Часы</b>\n";
    private static final String ENTRIES_TABLE_HEADER_MESSAGES_FULL = "<b>ID</b>\t|\t<b>Дата</b>\t|\t<b>Часы</b>\t|\t<b>Комментарий</b>\n";
    private static final String ENTRIES_TABLE_ROW = "<pre>%s\t|\t%.2f</pre>\n";
    private static final String ENTRIES_TABLE_ROW_FULL = "<pre>%d\t|\t%s\t|\t%.2f\t|\t%s</pre>\n";
    private static final String ENTRIES_TABLE_ROW_TOTAL = "<b>Часов</b>: <pre>%s</pre>\n";

    private static final String BORDER = "<pre>-------------------------------------------------------</pre>\n";

    private final UserRepository users = UserRepositoryImpl.getInstance();
    private final JobRepository jobs = JobRepositoryImpl.getInstance();

    @Override
    public List<TGMessage> invoke(Message message) {
        List<TGMessage> ret = new ArrayList<>();
        TGMessage msg = new TGMessage(MessageType.TEXT);
        msg.setParseMode(ParseMode.HTML);
        ret.add(msg);

        String _message = message.getText().replace("  ", " ");
        String[] splittedCommand = _message.split(" ");

        // Валидация аргументов
        if (splittedCommand.length < 2) {
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

            String command = splittedCommand[1];
            String project = splittedCommand.length > 2 ? splittedCommand[2] : null;
            Date timestamp = null;
            String date = StringUtils.extractByRegExp(_message, "-D(\\d{4}-\\d{2}-\\d{2})");
            if (date != null) {
                timestamp = new SimpleDateFormat(JobEntry.DATE_PATTERN).parse(date);
            }
            String comment = StringUtils.extractByRegExp(_message, "-C\"(.*)\"");
            Double editTimeEntry = null;
            String duration = StringUtils.extractByRegExp(_message, "-H(\\d+)");
            if (duration != null) {
                editTimeEntry = Double.parseDouble(duration);
            }
            String messageText = "";
            switch (command.toUpperCase()) {
                case "PUT": {
                    Double timeEntry = Double.parseDouble(splittedCommand[3]);
                    Long id = null;
                    if (timestamp == null) {
                        id = jobs.create(userId, new JobEntry(project, timeEntry, comment));
                        messageText = String.format(ENTRY_PUTTED, name, timeEntry, project, id);
                    } else {
                        id = jobs.create(userId, new JobEntry(project, timeEntry, comment, timestamp));
                        messageText = String.format(ENTRY_PUTTED_WITH_DATE, name, timeEntry, project, date, id);
                    }
                    break;
                }
                case "GET": {
                    String _id = StringUtils.extractByRegExp(_message, "-ID(\\d+)");
                    Long id = _id != null ? Long.parseLong(_id) : null;
                    List<JobEntry> entries = null;
                    if (id == null) {
                        entries = project == null ? jobs.list(userId) : jobs.list(userId, project);
                    } else {
                        JobEntry entry = jobs.get(userId, id);
                        if (entry != null) {
                            entries = Arrays.asList(entry);
                        }
                    }
                    if (entries != null) {
                        messageText = buildEntriesMessage(entries);
                        msg.setKeyboard(new JobEditEntryKeyboard(entries));
                    }
                    break;
                }
                case "GETSTAT": {
                    List<JobEntry> entries = project == null ? jobs.list(userId) : jobs.list(userId, project);
                    messageText = buildStatisticMessage(entries);
                    break;
                }
                case "GETSTATBYDAY": {
                    List<JobEntry> entries = project == null ? jobs.list(userId) : jobs.list(userId, project);
                    messageText = buildStatisticByDayMessage(entries);
                    break;
                }
                case "REMOVE": {
                    Long id = splittedCommand.length > 2 ? Long.parseLong(splittedCommand[2]) : null;
                    if (id == null) {
                        msg.setText(USAGE_TEXT);
                    } else {
                        boolean deleted = jobs.delete(userId, id);
                        messageText = deleted ? String.format(ENTRY_DELETED, name, id) : String.format(ENTRY_NOT_FOUND, id);
                    }
                    break;
                }
                case "EDIT": {
                    Long id = splittedCommand.length > 2 ? Long.parseLong(splittedCommand[2]) : null;
                    if (id == null || (editTimeEntry == null && comment == null && timestamp == null)) {
                        msg.setText(USAGE_TEXT);
                    } else {
                        boolean updated = jobs.update(userId, new JobEntry(id, editTimeEntry, comment, timestamp));
                        messageText = updated ? String.format(ENTRY_UPDATED, name, id) : String.format(ENTRY_NOT_FOUND, id);
                    }
                    break;
                }
                case "HELP":
                    msg.setText(USAGE_TEXT);
                    break;
                default:
                    messageText = String.format(UNKNOWN_COMMAND, command);
                    break;
            }
            msg.setText(messageText.isEmpty() ? "Записей не найдено" : messageText);
        } catch (Exception ex) {
            ex.printStackTrace();
            msg.setText(ex.getClass().getSimpleName() + "\t" + ex.getMessage());
        }
        return ret;
    }

    private String buildStatisticByDayMessage(List<JobEntry> entries) {
        StringBuilder sb = new StringBuilder();
        entries.stream().map(entry -> entry.getProject()).distinct().forEach((String project) -> {
            sb.append(String.format(ENTRIES_TITLE_MESSAGES, project));
            sb.append(ENTRIES_TABLE_HEADER_MESSAGES);
            sb.append(BORDER);
            Map<String, Double> data = entries.stream()
                    .filter(entry -> entry.getProject().equals(project))
                    .collect(Collectors.groupingBy(JobEntry::getDateAsString, Collectors.summingDouble(JobEntry::getDuration)));
            data.entrySet().forEach((entry) -> {
                sb.append(String.format(ENTRIES_TABLE_ROW, entry.getKey(), entry.getValue()));
                sb.append(BORDER);
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
        });
        return sb.toString();
    }

    private String buildEntriesMessage(List<JobEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        entries.stream().map(entry -> entry.getProject()).distinct().forEach(project -> {
            sb.append(String.format(ENTRIES_TITLE_MESSAGES, project));
            sb.append(ENTRIES_TABLE_HEADER_MESSAGES_FULL);
            sb.append(BORDER);
            entries.stream().filter(entry -> entry.getProject().equals(project)).forEach(entry -> {
                sb.append(String.format(ENTRIES_TABLE_ROW_FULL, entry.getId(), entry.getDateAsString(), entry.getDuration(), entry.getComment()));
                sb.append(BORDER);
            });
            sb.append("\n");
        });
        return sb.toString();
    }

}
