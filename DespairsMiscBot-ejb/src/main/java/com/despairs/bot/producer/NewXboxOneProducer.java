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

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author EKovtunenko
 */
public class NewXboxOneProducer implements MessageProducer {

    private static final String PRODUCER_ID = "NEWXBOXONE";

    private static final String URL = "http://www.newxboxone.ru/";

    private static final String ARTICLE = "article";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String HREF = "href";
    private static final String POST_REF = "article[id=%s] figure div a";

    @Inject
    private ProcessedReferenceRepository references;

    @Override
    public List<TGMessage> produce() throws Exception {
        List<TGMessage> ret = new ArrayList<>();
        Document doc = Jsoup.connect(URL).get();
        List<String> ids = doc.select(ARTICLE).stream()
                .map(e -> e.attr(ID))
                .filter(e -> !references.isReferenceStored(e, PRODUCER_ID))
                .collect(Collectors.toList());

        ids.forEach(id -> {
            doc.select(String.format(POST_REF, id)).forEach(s -> {
                TGMessage m = new TGMessage(MessageType.TEXT);
                m.setText(s.attr(TITLE));
                m.setLink(s.attr(HREF));
                ret.add(m);
            });
            references.createReference(id, PRODUCER_ID);
        });
        return ret;
    }

}
