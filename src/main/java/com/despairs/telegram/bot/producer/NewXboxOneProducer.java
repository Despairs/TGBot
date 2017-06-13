/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.producer;

import com.despairs.telegram.bot.utils.FileUtils;
import com.despairs.telegram.bot.model.TGMessage;
import com.despairs.telegram.bot.model.MessageType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author EKovtunenko
 */
public class NewXboxOneProducer implements MessageProducer {

    private static final String URL = "http://www.newxboxone.ru/";
    private static final String STORAGE_PATH = "newxboxone.txt";

    private static final String ARTICLE = "article";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String HREF = "href";
    private static final String POST_REF = "article[id=%s] figure div a";

    @Override
    public List<TGMessage> produce() throws Exception {
        List<TGMessage> ret = new ArrayList<>();
        List<String> filter = FileUtils.readAsList(STORAGE_PATH);
        Document doc = Jsoup.connect(URL).get();
        List<String> ids = doc.select(ARTICLE).stream().map(e -> e.attr(ID)).filter(e -> !filter.contains(e)).collect(Collectors.toList());
        ids.stream().forEach(id -> {
            doc.select(String.format(POST_REF, id)).forEach(s -> {
                TGMessage m = new TGMessage();
                m.setType(MessageType.TEXT);
                m.setText(s.attr(TITLE));
                m.setLink(s.attr(HREF));
                ret.add(m);
            });
            FileUtils.write(id, STORAGE_PATH);
        });
        return ret;
    }

}
