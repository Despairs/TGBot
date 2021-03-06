/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.producer;

import com.despairs.bot.db.repo.ProcessedReferenceRepository;
import com.despairs.bot.model.MessageType;
import com.despairs.bot.model.TGMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.iflex.commons.logging.Log4jLogger;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author EKovtunenko
 */
@Disabled
public class MiuiProducer implements MessageProducer {

    private static final String PRODUCER_ID = "MIUI";

    private static final String URL = "http://en.miui.com/";
    private static final String MI5_FORUM = "forum-92-1.html";
    public static final String MI5_FORUM_URL = URL + MI5_FORUM;

    private static final String TBODY = "tbody";
    private static final String ID = "id";
    private static final String HREF = "href";
    private static final String POST_REF = "tbody[id=%s] tr th div[class=avatarbox-info] div[class=sub-tit] a[class=s xst]";
    private static final String ROM = "ROM";

    @Inject
    private ProcessedReferenceRepository references;

    @Override
    public List<TGMessage> produce() throws Exception {
        List<TGMessage> ret = new ArrayList<>();
        Document doc;
        try {
            doc = Jsoup.connect(MI5_FORUM_URL).get();
            List<String> ids = doc.select(TBODY).stream()
                    .map(e -> e.attr(ID))
                    .filter(e -> !e.isEmpty() && !references.isReferenceStored(e, PRODUCER_ID))
                    .collect(Collectors.toList());

            ids.forEach(id -> {
                doc.select(String.format(POST_REF, id)).stream().filter(s -> s.text().contains(ROM)).distinct().forEach(s -> {
                    TGMessage m = new TGMessage(MessageType.TEXT);
                    m.setText(s.text());
                    m.setLink(URL + s.attr(HREF));
                    if (!ret.contains(m)) {
                        ret.add(m);
                    }
                });
                references.createReference(id, PRODUCER_ID);
            });
        } catch (IOException ex) {
            Log4jLogger.getLogger(this.getClass()).error(ex);
        }

        return ret;
    }

}
