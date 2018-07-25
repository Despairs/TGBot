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
@CommandCfg(alias = "Burger King", visible = true)
public class BurgerKingCommand implements Command {

    private static final String RESPONSE = "Текущие акции BurgerKing";
    private static final String URL = "https://burgerking.ru/";
    private static final String COUPONS_ENDPOINT = "bigboard/coupons";

    private static final String COUPON_REF = "img[class=coupon-img mt20]";
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
            couponRefs.stream()
                    .map(src -> src.replace("../", ""))
                    .forEach(src -> {
                        TGMessage m = new TGMessage(MessageType.PHOTO);
                        m.setLink(URL + src);
                        ret.add(m);
                    });
        } catch (IOException ex) {
            Log4jLogger.getLogger(BurgerKingCommand.class).error(ex);
        }

        return ret;
    }

}
