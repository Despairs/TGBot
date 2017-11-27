/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.commands.processor;

import com.despairs.telegram.bot.db.repo.SettingsRepository;
import com.despairs.telegram.bot.db.repo.UserRepository;
import com.despairs.telegram.bot.db.repo.impl.SettingsRepositoryImpl;
import com.despairs.telegram.bot.db.repo.impl.UserRepositoryImpl;
import com.despairs.telegram.bot.keyboard.RedmineGotIssueKeyboard;
import com.despairs.telegram.bot.model.MessageType;
import com.despairs.telegram.bot.model.TGMessage;
import com.despairs.telegram.bot.model.User;
import com.despairs.telegram.bot.utils.RedmineUtils;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Update;

/**
 *
 * @author EKovtunenko
 */
public class InlineCallbackQueryProcessor extends BaseProcessor {

    private final UserRepository users = UserRepositoryImpl.getInstance();

    private final CallbackQuery callback;

    public InlineCallbackQueryProcessor(Update update) {
        super(update.getCallbackQuery().getMessage());
        this.callback = update.getCallbackQuery();
    }

    @Override
    public void process() {
        try {
            String callbackMessage = callback.getData();
            if (callbackMessage.startsWith("assign_redmine_issue")) {
                User user = users.getUser(callback.getFrom().getId());
                if (user == null) {
                    TGMessage needRegisterMessage = new TGMessage(MessageType.TEXT);
                    needRegisterMessage.setText("Чтобы взять задачу в работу, необходимо зарегистрировать свой RedmineUserId, отправив боту комманду '/redmine@DespairsTestBot %d', где %d - ваш RedmineUserId (https://redminehost/users/%d))");
                    sender.sendTGMessage(needRegisterMessage, callback.getMessage().getChatId(), callback.getMessage().getMessageId());
                } else {
                    RedmineManager manager = RedmineUtils.getManager();
                    IssueManager issueManager = manager.getIssueManager();
                    String[] split = callbackMessage.split("_");

                    Issue issue = issueManager.getIssueById(Integer.parseInt(split[split.length - 1]));
                    issue.setAssigneeId(Integer.parseInt(user.getRedmineId()));
                    issueManager.update(issue);

                    Integer message_id = callback.getMessage().getMessageId();
                    Long chat_id = callback.getMessage().getChatId();
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
            ex.printStackTrace();
        }
    }

}
