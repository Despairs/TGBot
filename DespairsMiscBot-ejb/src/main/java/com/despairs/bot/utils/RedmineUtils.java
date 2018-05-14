/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.utils;

import com.despairs.bot.db.repo.SettingsRepository;
import com.despairs.bot.model.Settings;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import org.apache.http.impl.client.HttpClients;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;

/**
 * @author EKovtunenko
 */
@Singleton
public class RedmineUtils {

    @Inject
    private SettingsRepository settings;

    public RedmineManager getManager() throws SQLException {
        String url = settings.getValueV(Settings.REDMINE_HOST);
        String apiKey = settings.getValueV(Settings.REDMINE_API_KEY);
        return RedmineManagerFactory.
                createWithApiKey(url, apiKey, HttpClients
                        .custom()
                        .setSSLSocketFactory(HttpsUtils.getSSLConnectionSocketFactory())
                        .build());
    }

}
