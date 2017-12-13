/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.producer;

import com.despairs.bot.db.repo.ProcessedReferenceRepository;
import com.despairs.bot.db.repo.SettingsRepository;
import com.despairs.bot.keyboard.RedmineIssueKeyboard;
import com.despairs.bot.model.MessageType;
import com.despairs.bot.model.ParseMode;
import com.despairs.bot.model.Settings;
import com.despairs.bot.model.TGMessage;
import com.despairs.bot.utils.RedmineUtils;
import com.despairs.bot.utils.StringUtils;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.internal.ResultsWrapper;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import ru.iflex.commons.logging.Log4jLogger;

/**
 *
 * @author EKovtunenko
 */
public class RedmineIssueProducer implements MessageProducer {

    private static final String PRODUCER_ID = "REDMINE_%s";

    private static final String MESSAGE_PATTERN = "<b>Заголовок</b>: <pre>%s</pre>\n"
            + "<b>Автор</b>: <pre>%s</pre>\n"
            + "<b>Назначена</b>: <pre>%s</pre>\n"
            + "<b>Дата начала</b>: <pre>%s</pre>\n"
            + "<b>Дата завершения</b>: <pre>%s</pre>\n"
            + "<b>Приоритет</b>: <pre>%s</pre>\n"
            + "<b>Описание</b>: <pre>%s</pre>\n";

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Inject
    private SettingsRepository settings;
    @Inject
    private ProcessedReferenceRepository references;
    @Inject
    private RedmineUtils redmineUtils;

    private String url;
    private String issue_url;
    private String channelId;
    private List<String> users;

    @PostConstruct
    public void init() {
        try {
            url = settings.getValueV(Settings.REDMINE_HOST);
            issue_url = url + "issues/%d";
            channelId = settings.getValueV(Settings.REDMINE_CHANNEL_ID);
            users = Arrays.asList(settings.getValueV(Settings.REDMINE_ISSUES_ASSIGNED_TO_USERS).split(","));
        } catch (Exception ex) {
            Log4jLogger.getLogger(this.getClass()).error(ex);
        }
    }

    @Override
    public List<TGMessage> produce() throws Exception {
        List<TGMessage> ret = new ArrayList<>();

        RedmineManager mgr = redmineUtils.getManager();
        IssueManager issueManager = mgr.getIssueManager();
        users.forEach(user -> {
            try {
                ResultsWrapper<Issue> issuesWrapper = issueManager.getIssues(new Params().add("assigned_to_id", user));
                List<Issue> issues = issuesWrapper.getResults();
                issues.stream()
                        .filter(issue -> {
                            try {
                                return !references.isReferenceStored(String.valueOf(issue.getId()), String.format(PRODUCER_ID, user));
                            } catch (SQLException ex) {
                                Log4jLogger.getLogger(this.getClass()).error(ex);
                                return false;
                            }
                        })
                        .forEach(_issue -> {
                            TGMessage m = new TGMessage(MessageType.TEXT);
                            m.setText(buildMessage(_issue));
                            m.setLink(String.format(issue_url, _issue.getId()));
                            m.setParseMode(ParseMode.HTML);
                            m.setChatId(channelId);
                            m.setKeyboard(new RedmineIssueKeyboard(_issue.getId()));
                            ret.add(m);
                            try {
                                references.createReference(String.valueOf(_issue.getId()), String.format(PRODUCER_ID, user));
                            } catch (SQLException ex) {
                                Log4jLogger.getLogger(this.getClass()).error(ex);
                            }
                        });
            } catch (RedmineException ex) {
                Log4jLogger.getLogger(this.getClass()).error(ex);
            }
        });
        return ret;
    }

    private String buildMessage(Issue issue) {
        String ret = String.format(MESSAGE_PATTERN,
                StringUtils.normalize(issue.getSubject()),
                issue.getAuthorName(),
                issue.getAssigneeName(),
                new SimpleDateFormat(DATE_PATTERN).format(issue.getCreatedOn()),
                issue.getDueDate() != null ? new SimpleDateFormat(DATE_PATTERN).format(issue.getDueDate()) : "",
                issue.getPriorityText(),
                StringUtils.normalize(issue.getDescription()));
        return ret;
    }

}
