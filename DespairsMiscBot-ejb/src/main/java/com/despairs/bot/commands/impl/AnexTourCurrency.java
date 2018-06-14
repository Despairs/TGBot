package com.despairs.bot.commands.impl;

import com.despairs.bot.commands.Command;
import com.despairs.bot.commands.CommandCfg;
import com.despairs.bot.model.MessageType;
import com.despairs.bot.model.ParseMode;
import com.despairs.bot.model.TGMessage;
import com.google.gson.GsonBuilder;
import feign.Feign;
import feign.Headers;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.telegram.telegrambots.api.objects.Message;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author EKovtunenko
 */
@CommandCfg(alias = "Курс AnexTour", visible = true)
public class AnexTourCurrency implements Command {

    private static final AnexTourAPI API = Feign.builder()
            .decoder(new GsonDecoder())
            .encoder(new GsonEncoder(new GsonBuilder().setDateFormat("yyyyMMdd").create()))
            .target(AnexTourAPI.class, "http://www.anextour.com");

    @Override
    public List<TGMessage> invoke(Message message) {
        return API.getCurrency(new Date()).stream()
                .flatMap(Collection::stream)
                .map(currency -> {
                    TGMessage m = new TGMessage(MessageType.TEXT);
                    m.setParseMode(ParseMode.HTML);
                    m.setText(currency.toString());
                    return m;
                })
                .collect(Collectors.toList());
    }

    private interface AnexTourAPI {
        @Headers("User-Agent: Chrome/67.0.3396.62")
        @RequestLine("GET /Api/GetCurrency?market=ANEXRU&date={date}")
        List<List<Currency>> getCurrency(Date date);

        class Currency {
            BigDecimal usd;
            BigDecimal eur;
            String date;

            @Override
            public String toString() {
                return "Курс на <b>" + date + "</b>:\n" +
                        "<b>USD</b>: <pre>" + usd + "</pre>\n" +
                        "<b>EURO</b>: <pre>" + eur + "</pre>";
            }
        }
    }

}

