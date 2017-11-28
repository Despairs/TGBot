/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.utils;

import com.despairs.telegram.bot.db.repo.SettingsRepository;
import com.despairs.telegram.bot.db.repo.impl.SettingsRepositoryImpl;
import com.despairs.telegram.bot.model.Settings;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import java.sql.SQLException;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author EKovtunenko
 */
public class RedmineUtils {

    private static final SettingsRepository settings = SettingsRepositoryImpl.getInstance();

    public static RedmineManager getManager() throws SQLException {
        String url = settings.getValueV(Settings.REDMINE_HOST);
        String apiKey = settings.getValueV(Settings.REDMINE_API_KEY);
        return RedmineManagerFactory.
                createWithApiKey(url, apiKey, HttpClients
                        .custom()
                        .setSSLSocketFactory(HttpsUtils.getSSLConnectionSocketFactory())
                        .build());
    }

}
