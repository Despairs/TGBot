/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.producer;

import com.despairs.telegram.bot.model.MessageType;
import com.despairs.telegram.bot.model.TGMessage;
import com.despairs.telegram.bot.utils.FileUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author EKovtunenko
 */
public class MiuiProducer implements MessageProducer {

    private static final String URL = "http://en.miui.com/";
    private static final String MI5S_FORUM = "forum-115-1.html";
    private static final String MI5_FORUM = "forum-92-1.html";
    private static final List<String> FORUMS = Arrays.asList(MI5S_FORUM, MI5_FORUM);

    private static final String STORAGE_PATH = "miui.txt";

    private static final String TBODY = "tbody";
    private static final String ID = "id";
    private static final String HREF = "href";
    private static final String POST_REF = "tbody[id=%s] tr th div[class=avatarbox-info] div[class=sub-tit] a[class=s xst]";
    private static final String ROM = "ROM";

    @Override
    public List<TGMessage> produce() throws Exception {
        List<TGMessage> ret = new ArrayList<>();
        List<String> filter = FileUtils.readAsList(STORAGE_PATH);
        FORUMS.parallelStream().forEach(f -> {
            Document doc;
            try {
                doc = Jsoup.connect(URL + f).get();
                List<String> ids = doc.select(TBODY).stream().map(e -> e.attr(ID)).filter(e -> !e.isEmpty() && !filter.contains(e)).collect(Collectors.toList());
                ids.forEach(id -> {
                    doc.select(String.format(POST_REF, id)).stream().filter(s -> s.text().contains(ROM)).forEach(s -> {
                        TGMessage m = new TGMessage(MessageType.TEXT);
                        m.setText(s.text());
                        m.setLink(URL + s.attr(HREF));
                        ret.add(m);
                    });
                    FileUtils.write(id, STORAGE_PATH);
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return ret;
    }

}
