package com.despairs.bot.producer;

import com.despairs.bot.clients.AnexTour;
import com.despairs.bot.db.repo.SettingsRepository;
import com.despairs.bot.model.MessageType;
import com.despairs.bot.model.ParseMode;
import com.despairs.bot.model.TGMessage;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author EKovtunenko
 */
public class AnexTourCurrencyProducer implements MessageProducer {

    private static final Map<LocalDate, Boolean> PROCESSED_DAYS = new HashMap<>();

    private static final String MESSAGE_PATTERN = "Сегодняшний курс доллара у АнексТур меньше вчерашнего!\n"
            + "Вчера: <pre>%s</pre>\n"
            + "Сегодня: <pre>%s</pre>\n"
            + "Дельта: <pre>%s</pre>";
    private static final String ANEX_CURRENCY_CHANNEL = "ANEX_CURRENCY_CHANNEL";

    @Inject
    private SettingsRepository settings;

    @Override
    public List<TGMessage> produce() throws Exception {
        TGMessage m = null;
        LocalDate now = LocalDate.now();
        if (LocalDateTime.now().getHour() >= 10) {
            if (!PROCESSED_DAYS.containsKey(now)) {
                BigDecimal currentValue = getUsdValue(now);
                BigDecimal previousValue = getUsdValue(now.minusDays(1));
                if (currentValue.compareTo(previousValue) < 0) {
                    m = new TGMessage(MessageType.TEXT);
                    m.setParseMode(ParseMode.HTML);
                    m.setText(String.format(MESSAGE_PATTERN, previousValue, currentValue, currentValue.subtract(previousValue)));
                    m.setChatId(settings.getValueV(ANEX_CURRENCY_CHANNEL));
                }
                PROCESSED_DAYS.put(now, Boolean.TRUE);
            }
        }
        return m != null ? Collections.singletonList(m) : Collections.emptyList();
    }

    private BigDecimal getUsdValue(LocalDate date) throws Exception {
        Optional<BigDecimal> usd = AnexTour.API.getCurrency(date).stream()
                .flatMap(Collection::stream)
                .map(AnexTour.Currency::getUsd)
                .findFirst();
        return usd.orElseThrow(() -> new Exception("Can't get currency for date " + date));
    }
}
