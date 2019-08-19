package com.despairs.bot.producer;

import com.despairs.bot.db.repo.ProcessedReferenceRepository;
import com.despairs.bot.model.MessageType;
import com.despairs.bot.model.TGMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author EKovtunenko
 */
public class DTFMessageProducer implements MessageProducer {
    private static final String PRODUCER_ID = "DTF";

    private static final String URL = "https://dtf.ru/";
    private static final String POST = "div.feed__item";
    private static final String CONTENT = "div.content-feed";
    private static final String CONTENT_HEADER = "div.content-header";
    private static final String CONTENT_LINK = "a.content-feed__link";

    private static final String ID = "data-content-id";
    private static final String TITLE = "h2";
    private static final String HREF = "href";

    @Inject
    private ProcessedReferenceRepository references;

    @Override
    public List<TGMessage> produce() throws Exception {
        List<TGMessage> ret = new ArrayList<>();
        Document doc = Jsoup.connect(URL).get();

        doc.select(POST).stream()
                .map(e -> {
                    Elements post = e.select(CONTENT);
                    String id = post.attr(ID);
                    return new Post(id, post);
                })
                .filter(post -> !references.isReferenceStored(post.id, PRODUCER_ID))
                .forEach(post -> {
                            TGMessage m = new TGMessage(MessageType.TEXT);
                            m.setText(post.content.select(CONTENT_HEADER).select(TITLE).text());
                            m.setLink(post.content.select(CONTENT_LINK).attr(HREF));
                            ret.add(m);
                            references.createReference(post.id, PRODUCER_ID);
                        }
                );
        return ret;
    }

    private static class Post {
        private String id;
        private Elements content;

        public Post(String id, Elements content) {
            this.id = id;
            this.content = content;
        }
    }
}
