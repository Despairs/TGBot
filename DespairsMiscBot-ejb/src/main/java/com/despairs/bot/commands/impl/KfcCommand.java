/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.commands.impl;

import com.despairs.bot.commands.Command;
import com.despairs.bot.commands.CommandCfg;
import com.despairs.bot.model.MessageType;
import com.despairs.bot.model.ParseMode;
import com.despairs.bot.model.TGMessage;
import com.despairs.bot.utils.HttpsUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.api.objects.Message;
import ru.iflex.commons.logging.Log4jLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author EKovtunenko
 */
@CommandCfg(alias = "KFC", visible = true)
public class KfcCommand implements Command {

    private static final String RESPONSE = "Текущие акции KFC";
    private static final String URL = "https://www.kfc.ru/coupons";

    private static final String COUPON_REF = "div[class=app__grid__item app__grid__item_medium]";
    private static final String STYLE = "style";

    @Override
    public List<TGMessage> invoke(Message message) {
        List<TGMessage> ret = new ArrayList<>();
        TGMessage msg = new TGMessage(MessageType.TEXT);
        msg.setText(RESPONSE);
        ret.add(msg);

        try {
            HttpsUtils.trustAllCerts();

            Document doc = Jsoup.connect(URL).get();
            Elements elements = doc.select(COUPON_REF);

            Map<String, String> links = elements.stream()
                    .collect(Collectors.toMap(
                            e -> e.selectFirst("div[class=app__grid__item-content]").attr(STYLE).replaceAll("background-image:url\\((.*)\\)", "$1"),
                            e -> description(e) +
                                    "\n" +
                                    fullPrice(e) + " -> " + discountPrice(e) +
                                    "\n" +
                                    coupon(e)
                            )
                    );

            links.forEach((link, text) -> {
                TGMessage m = new TGMessage(MessageType.PHOTO);
                m.setLink(link);
                m.setText(text);
                m.setParseMode(ParseMode.HTML);
                ret.add(m);
            });
        } catch (IOException ex) {
            Log4jLogger.getLogger(this.getClass()).error(ex);
        }

        return ret;
    }

    private String coupon(Element e) {
        return "Купон: " + getTextFromFirstNode(e, "div[class=promo-list__caption]");
    }

    private String description(Element e) {
        return getTextFromFirstNode(e, "p[class=promo-list__description]");
    }

    private String discountPrice(Element e) {
        return getTextFromFirstNode(e, "div[class=item-info__price app__delivery__price]");
    }

    private String fullPrice(Element e) {
        return getTextFromFirstNode(e, "span[class=item-info__label_discount-text]");
    }

    private String getTextFromFirstNode(Element e, String s) {
        return e.selectFirst(s).wholeText();
    }

}
//<div class="app__grid__item app__grid__item_medium">
//<div class="app__grid__item-content" style="background-image:url(https://statickfc.cdnvideo.ru/coupons/coupon_5c0f1cba5b76d.jpg)">
//<span class="item-info__label_discount-text">292</span>
//<div class="item-info__price app__delivery__price">234
//<p class="promo-list__description">Донат Яблоко-Корица + Капучино 0,3 л</p>