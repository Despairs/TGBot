package com.despairs.bot.commands.impl;

import com.despairs.bot.clients.AnexTour;
import com.despairs.bot.commands.Command;
import com.despairs.bot.commands.CommandCfg;
import com.despairs.bot.model.MessageType;
import com.despairs.bot.model.ParseMode;
import com.despairs.bot.model.TGMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author EKovtunenko
 */
@CommandCfg(alias = "Курс AnexTour", visible = true)
public class AnexTourCurrency implements Command {

    @Override
    public List<TGMessage> invoke(Message message) {
        return AnexTour.API.getCurrency(LocalDate.now()).stream()
                .flatMap(Collection::stream)
                .map(currency -> {
                    TGMessage m = new TGMessage(MessageType.TEXT);
                    m.setParseMode(ParseMode.HTML);
                    m.setText(currency.toString());
                    return m;
                })
                .collect(Collectors.toList());
    }

}

