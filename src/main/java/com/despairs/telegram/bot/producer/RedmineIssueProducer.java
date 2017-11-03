/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.producer;

import com.despairs.telegram.bot.model.MessageType;
import com.despairs.telegram.bot.model.ParseMode;
import com.despairs.telegram.bot.model.TGMessage;
import com.despairs.telegram.bot.utils.FileUtils;
import com.despairs.telegram.bot.utils.HttpsUtils;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.internal.ResultsWrapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author EKovtunenko
 */
public class RedmineIssueProducer implements MessageProducer {

    private static final String CFG_PATH = "redmine.cfg";
    
    private static final List<String> CFG = FileUtils.readAsList(CFG_PATH);
    
    private static final String URL = CFG.get(0);
    private static final String ISSUE_URL = URL + "issues/%d";
    private static final String STORAGE_PATH = "redmine_%s";

    private static final String MESSAGE_PATTERN = "<b>Заголовок</b>: <pre>%s</pre>\n"
            + "<b>Автор</b>: <pre>%s</pre>\n"
            + "<b>Назначена</b>: <pre>%s</pre>\n"
            + "<b>Дата начала</b>: <pre>%s</pre>\n"
            + "<b>Дата завершения</b>: <pre>%s</pre>\n"
            + "<b>Приоритет</b>: <pre>%s</pre>\n"
            + "<b>Описание</b>: <pre>%s</pre>\n";

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String CHANNEL_ID = CFG.get(1);

    private static final List<String> USERS = Arrays.asList("93", "95"); //93 - Developer Java, 95 - Архитектор ESB

    @Override
    public List<TGMessage> produce() throws Exception {
        List<TGMessage> ret = new ArrayList<>();

        RedmineManager mgr = RedmineManagerFactory.
                createWithApiKey(URL, CFG.get(2), HttpClients
                        .custom()
                        .setSSLSocketFactory(HttpsUtils.getSSLConnectionSocketFactory())
                        .build());
        IssueManager issueManager = mgr.getIssueManager();
        USERS.forEach(user -> {
            List<String> filter = FileUtils.readAsList(String.format(STORAGE_PATH, user));
            try {
                ResultsWrapper<Issue> issuesWrapper = issueManager.getIssues(new Params().add("assigned_to_id", user));
                List<Issue> issues = issuesWrapper.getResults();
                issues.parallelStream().filter(issue -> !filter.contains(String.valueOf(issue.getId()))).forEach(issue -> {
                    TGMessage m = new TGMessage(MessageType.TEXT);
                    m.setText(buildMessage(issue));
                    m.setLink(String.format(ISSUE_URL, issue.getId()));
                    m.setParseMode(ParseMode.HTML);
                    m.setChatId(CHANNEL_ID);
                    ret.add(m);
                    FileUtils.write(String.valueOf(issue.getId()), String.format(STORAGE_PATH, user));
                });
            } catch (RedmineException ex) {
                ex.printStackTrace();
            }
        });
        return ret;
    }

    private String buildMessage(Issue issue) {
        String description = issue.getDescription().replaceAll("&", "amp&;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        if (description.length() > 4096) {
            description = description.substring(0, 4090).concat("...");
        }
        String ret = String.format(MESSAGE_PATTERN,
                issue.getSubject(),
                issue.getAuthorName(),
                issue.getAssigneeName(),
                new SimpleDateFormat(DATE_PATTERN).format(issue.getCreatedOn()),
                issue.getDueDate() != null ? new SimpleDateFormat(DATE_PATTERN).format(issue.getDueDate()) : "",
                issue.getPriorityText(),
                description);
        return ret;
    }

}
