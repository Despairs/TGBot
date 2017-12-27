/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.commands.impl;

import com.despairs.bot.commands.Command;
import com.despairs.bot.commands.CommandCfg;
import com.despairs.bot.commands.ScopeType;
import com.despairs.bot.db.repo.SalaryRepository;
import com.despairs.bot.db.repo.impl.SalaryRepositoryImpl;
import com.despairs.bot.model.MessageType;
import com.despairs.bot.model.ParseMode;
import com.despairs.bot.model.SalaryEntry;
import com.despairs.bot.model.TGMessage;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.api.objects.Message;
import ru.iflex.commons.logging.Log4jLogger;

/**
 *
 * @author EKovtunenko
 */
@CommandCfg(alias = "SALARY#", scope = ScopeType.ADMIN)
public class SalaryActionCommand implements Command {

    private static final String ENTRY_PUTTED = "Добавлено за %s. <b>ID</b> = %d";
    private static final String ENTRY_DELETED = "Удалено";

    private static final String ENTRY_NOT_FOUND = "Запись с <b>ID</b> = %d не найдена";

    private static final String ENTRIES_TITLE = "<b>Период</b>: <pre>%s</pre>\n";
    private static final String ENTRIES_TABLE_HEADER = "<b>ID</b>\t|\t<b>Дата</b>\t|\t<b>Сумма</b>\t|\t<b>Комментарий</b>\n";
    private static final String ENTRIES_TABLE_ROW = "<pre>%d\t|\t%s\t|\t%.2f\t|\t%s</pre>\n";

    private static final String ENTRIES_TABLE_ROW_AGGREGATION = "<b>Всего</b>: <pre>%.2f</pre>\n";

    private static final String BORDER = "<pre>-------------------------------------------------------</pre>\n";

    private final SalaryRepository salary = SalaryRepositoryImpl.getInstance();

    @Override
    public List<TGMessage> invoke(Message message) {
        List<TGMessage> ret = new ArrayList<>();
        TGMessage msg = new TGMessage(MessageType.TEXT);
        msg.setParseMode(ParseMode.HTML);
        msg.setReplyTo(message.getMessageId());
        ret.add(msg);

        String msgText = null;
        try {
            String[] args = message.getText().split("#");
            String action = args[1];
            switch (action) {
                case "INPUT": {
                    if (args.length < 4) {
                        throw new Exception("Invalid arguments: must be >= 4");
                    }
                    Double amount = Double.parseDouble(args[3]);
                    String period = args[2];
                    String comment = null;
                    if (args.length == 5) {
                        comment = args[4];
                    }
                    Long id = salary.create(new SalaryEntry(amount, period, comment));
                    msgText = String.format(ENTRY_PUTTED, period, id);
                    break;
                }
                case "VIEW": {
                    String type = args[2];
                    String period = args[3];
                    switch (type) {
                        case "ALL":
                            List<SalaryEntry> list = salary.list(period);
                            msgText = buildViewAllMessage(list);
                            break;
                        case "AGGREGATION":
                            Double sum = salary.sumByPeriod(period);
                            msgText = buildViewAggregationMessage(period, sum);
                            break;
                    }
                    break;
                }
                case "DELETE": {
                    Long id = Long.parseLong(args[2]);
                    boolean deleted = salary.delete(id);
                    msgText = deleted ? ENTRY_DELETED : String.format(ENTRY_NOT_FOUND, id);
                    break;
                }
            }
        } catch (Exception ex) {
            Log4jLogger.getLogger(this.getClass()).error(ex);
            msgText = ex.getClass().getSimpleName() + ":\t" + ex.getMessage();
        }

        msg.setText(msgText == null ? "Записей не найдено" : msgText);
        return ret;
    }

    private String buildViewAllMessage(List<SalaryEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        entries.stream().map(entry -> entry.getPeriod()).distinct().forEach(project -> {
            sb.append(String.format(ENTRIES_TITLE, project));
            sb.append(ENTRIES_TABLE_HEADER);
            sb.append(BORDER);
            entries.stream().filter(entry -> entry.getPeriod().equals(project)).forEach(entry -> {
                sb.append(String.format(ENTRIES_TABLE_ROW, entry.getId(), entry.getDateAsString(), entry.getAmount(), entry.getComment()));
                sb.append(BORDER);
            });
            sb.append("\n");
        });
        return sb.toString();
    }

    private String buildViewAggregationMessage(String period, Double amount) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(ENTRIES_TITLE, period));
        sb.append(String.format(ENTRIES_TABLE_ROW_AGGREGATION, amount));
        return sb.toString();
    }

}
