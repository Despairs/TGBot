/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.commands.impl;

import com.despairs.bot.commands.Command;
import com.despairs.bot.commands.CommandCfg;
import com.despairs.bot.model.MessageType;
import com.despairs.bot.model.TGMessage;
import com.despairs.bot.utils.HttpsUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.telegram.telegrambots.api.objects.Message;
import ru.iflex.commons.logging.Log4jLogger;

/**
 *
 * @author EKovtunenko
 */
@CommandCfg(alias = "KFC", visible = true)
public class KfcCommand implements Command {

    private static final String RESPONSE = "Текущие акции KFC";
    private static final String URL = "https://www.kfc.ru/";
    private static final String COUPONS_ENDPOINT = "promo/74";

    private static final String COUPON_REF = "li[class=coupon-list__item] img";
    private static final String SRC = "src";

    @Override
    public List<TGMessage> invoke(Message message) {
        List<TGMessage> ret = new ArrayList<>();
        TGMessage msg = new TGMessage(MessageType.TEXT);
        msg.setText(RESPONSE);
        ret.add(msg);

        try {
            HttpsUtils.trustAllCerts();
            Document doc = Jsoup.connect(URL + COUPONS_ENDPOINT).get();
            List<String> couponRefs = doc.select(COUPON_REF).stream().map(e -> e.attr(SRC)).collect(Collectors.toList());
            couponRefs.forEach(src -> {
                TGMessage m = new TGMessage(MessageType.PHOTO);
                m.setLink(src);
                ret.add(m);
            });
        } catch (IOException ex) {
            Log4jLogger.getLogger(this.getClass()).error(ex);
        }

        return ret;
    }

}