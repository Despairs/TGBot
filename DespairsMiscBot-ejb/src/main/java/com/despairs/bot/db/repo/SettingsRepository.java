/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.db.repo;

import java.sql.SQLException;

/**
 *
 * @author EKovtunenko
 */
public interface SettingsRepository {

    String getValueV(String id) throws SQLException;

    Long getValueN(String id) throws SQLException;

    Boolean getValueB(String id) throws SQLException;
}
