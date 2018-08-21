package com.despairs.bot.clients;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author EKovtunenko
 */
public interface AnexTour {

    AnexTour API = Feign.builder()
            .decoder(new GsonDecoder())
            .target(AnexTour.class, "https://www.anextour.com");

    @Headers("User-Agent: Chrome/67.0.3396.62")
    @RequestLine("GET /api/GetCurrency?market=ANEXRU&date={date}")
    List<List<Currency>> getCurrency(@Param("date") LocalDate date);

    @Data
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
