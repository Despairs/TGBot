/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.impl;

import com.despairs.telegram.bot.commands.VisibleCommand;
import com.despairs.telegram.bot.model.MessageType;
import com.despairs.telegram.bot.model.TGMessage;
import com.despairs.telegram.bot.utils.HttpsUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 * @author EKovtunenko
 */
public class KfcCommand implements VisibleCommand {

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
            ex.printStackTrace();
        }

        return ret;
    }

}
