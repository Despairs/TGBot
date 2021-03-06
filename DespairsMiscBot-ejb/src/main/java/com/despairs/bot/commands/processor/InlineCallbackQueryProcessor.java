/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.commands.processor;

import com.despairs.bot.commands.impl.JobInlineCommand;
import com.despairs.bot.commands.impl.SalaryInlineCommand;
import com.despairs.bot.keyboard.RedmineGotIssueKeyboard;
import com.despairs.bot.model.MessageType;
import com.despairs.bot.model.TGMessage;
import com.despairs.bot.utils.RedmineUtils;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import javax.inject.Inject;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import ru.iflex.commons.logging.Log4jLogger;

/**
 *
 * @author EKovtunenko
 */
public class InlineCallbackQueryProcessor extends BaseProcessor {

    @Inject
    private RedmineUtils redmineUtils;
    
    private CallbackQuery callback;

    public BaseProcessor setCallbackQuery(CallbackQuery callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public void process() {
        Integer message_id = callback.getMessage().getMessageId();
        Long chat_id = callback.getMessage().getChatId();
        String callbackMessage = callback.getData();
        try {
            if (callbackMessage.startsWith("SALARY#")) {
                new SalaryInlineCommand(callback, sender).invoke(message);
            }
            if (callbackMessage.startsWith("JOB#")) {
                new JobInlineCommand(callback, sender, user).invoke(message);
            } else if (callbackMessage.startsWith("assign_redmine_issue")) {
                if (user.getRedmineId() == null) {
                    TGMessage needRegisterMessage = new TGMessage(MessageType.TEXT);
                    needRegisterMessage.setText("Чтобы взять задачу в работу, необходимо зарегистрировать свой RedmineUserId, отправив боту комманду '/redmine@DespairsTestBot %d', где %d - ваш RedmineUserId (https://redminehost/users/%d))");
                    sender.sendTGMessage(needRegisterMessage, message.getChatId(), message.getMessageId());
                } else {
                    RedmineManager manager = redmineUtils.getManager();
                    IssueManager issueManager = manager.getIssueManager();
                    String[] split = callbackMessage.split("_");

                    Issue issue = issueManager.getIssueById(Integer.parseInt(split[split.length - 1]));
                    issue.setAssigneeId(Integer.parseInt(user.getRedmineId()));
                    issueManager.update(issue);

                    EditMessageReplyMarkup editKeyboard = new EditMessageReplyMarkup()
                            .setChatId(chat_id)
                            .setMessageId(message_id)
                            .setReplyMarkup(new RedmineGotIssueKeyboard(user.getName()));
                    sender.executeMethod(editKeyboard);
                }
            } else if (callbackMessage.startsWith("redmine_issue_already_assigned")) {
                AnswerCallbackQuery callbackAnswer = new AnswerCallbackQuery()
                        .setCallbackQueryId(callback.getId())
                        .setText("Задача уже назначена");
                sender.executeMethod(callbackAnswer);
            }
        } catch (Exception ex) {
            Log4jLogger.getLogger(this.getClass()).error(ex);
        }
    }

}
