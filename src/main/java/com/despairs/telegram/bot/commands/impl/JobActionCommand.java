/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.impl;

import com.despairs.telegram.bot.commands.Command;
import com.despairs.telegram.bot.db.repo.JobRepository;
import com.despairs.telegram.bot.db.repo.impl.JobRepositoryImpl;
import com.despairs.telegram.bot.model.JobEntry;
import com.despairs.telegram.bot.model.MessageType;
import com.despairs.telegram.bot.model.ParseMode;
import com.despairs.telegram.bot.model.TGMessage;
import com.despairs.telegram.bot.model.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
public class JobActionCommand implements Command {

    private static final String UNKNOWN_COMMAND = "Неизвестная команда: %s";

    private static final String ENTRY_PUTTED = "<b>%s</b> добавил %.2f ч. в проект %s. <b>ID</b> = %d";
    private static final String ENTRY_PUTTED_WITH_DATE = "<b>%s</b> добавил %.2f ч. в проект %s за %s. <b>ID</b> = %d";
    private static final String ENTRY_DELETED = "<b>%s</b> удалил запись с <b>ID</b> = %d";
    private static final String ENTRY_UPDATED = "<b>%s</b> обновил запись с <b>ID</b> = %d";

    private static final String ENTRY_NOT_FOUND = "Запись с <b>ID</b> = %d не найдена";

    private static final String ENTRIES_TITLE_MESSAGES = "<b>Проект</b>: <pre>%s</pre>\n";
    private static final String ENTRIES_TABLE_HEADER_MESSAGES_FULL = "<b>ID</b>\t|\t<b>Дата</b>\t|\t<b>Часы</b>\t|\t<b>Комментарий</b>\n";
    private static final String ENTRIES_TABLE_ROW_FULL = "<pre>%d\t|\t%s\t|\t%.2f\t|\t%s</pre>\n";
    private static final String ENTRIES_TABLE_ROW_TOTAL = "<b>Часов</b>: <pre>%s</pre>\n";

    private static final String BORDER = "<pre>-------------------------------------------------------</pre>\n";

    private final JobRepository jobs = JobRepositoryImpl.getInstance();

    private final User user;

    public JobActionCommand(User user) {
        this.user = user;
    }

    @Override
    public List<TGMessage> invoke(Message message) {
        List<TGMessage> ret = new ArrayList<>();
        TGMessage msg = new TGMessage(MessageType.TEXT);
        msg.setParseMode(ParseMode.HTML);
        msg.setReplyTo(message.getMessageId());
        ret.add(msg);

        String msgText = null;
        try {
            String name = user.getName();
            Integer userId = user.getId();

            String[] args = message.getText().split("#");
            String action = args[1];
            switch (action) {
                case "INPUT": {
                    //JOB#INPUT#PGP#HOURS#[DATE{yyyy-MM-dd}]#[COMMENT]
                    if (args.length < 4) {
                        throw new Exception("Invalid arguments: must be >= 4");
                    }
                    String project = args[2];
                    Double hours = Double.parseDouble(args[3]);
                    Date timestamp = null;
                    if (args.length >= 5 && !args[4].isEmpty()) {
                        timestamp = new SimpleDateFormat(JobEntry.DATE_PATTERN).parse(args[4]);
                    }
                    String comment = null;
                    if (args.length == 6) {
                        comment = args[5];
                    }
                    Long id;
                    if (timestamp == null) {
                        id = jobs.create(userId, new JobEntry(project, hours, comment));
                        msgText = String.format(ENTRY_PUTTED, name, hours, project, id);
                    } else {
                        id = jobs.create(userId, new JobEntry(project, hours, comment, timestamp));
                        msgText = String.format(ENTRY_PUTTED_WITH_DATE, name, hours, project, args[4], id);
                    }
                    break;
                }
                case "VIEW": {
                    //JOB#VIEW#ALL#ALL_TIME#PGP#
                    String type = args[2];
                    String paymentType = args[3];
                    String project = args[4];
                    List<JobEntry> entries = paymentType.equals("ALL_TIME") ? jobs.list(userId, project) : jobs.listFromLastPayment(userId, project);
                    switch (type) {
                        case "ALL":
                            msgText = buildEntriesMessage(entries);
                            break;
                        case "AGGREGATION":
                            msgText = buildStatisticMessage(entries);
                            break;
                    }
                    break;
                }
                case "DELETE": {
                    Long id = Long.parseLong(args[2]);
                    boolean deleted = jobs.delete(userId, id);
                    msgText = deleted ? String.format(ENTRY_DELETED, name, id) : String.format(ENTRY_NOT_FOUND, id);
                    break;
                }
                case "UPDATE": {
                    // JOB#UPDATE#ID#HOURS#[DATE{yyyy-MM-dd}]#[COMMENT]
                    Long id = Long.parseLong(args[2]);
                    Double hours = Double.parseDouble(args[3]);
                    Date timestamp = null;
                    if (args.length >= 5) {
                        timestamp = new SimpleDateFormat(JobEntry.DATE_PATTERN).parse(args[4]);
                    }
                    String comment = null;
                    if (args.length == 6) {
                        comment = args[5];
                    }
                    boolean updated = jobs.update(userId, new JobEntry(id, hours, comment, timestamp));
                    msgText = updated ? String.format(ENTRY_UPDATED, name, id) : String.format(ENTRY_NOT_FOUND, id);
                    break;
                }
                default:
                    msgText = String.format(UNKNOWN_COMMAND, action);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            msgText = ex.getClass().getSimpleName() + ":\t" + ex.getMessage();
        }

        msg.setText(msgText == null ? "Записей не найдено" : msgText);
        return ret;
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
