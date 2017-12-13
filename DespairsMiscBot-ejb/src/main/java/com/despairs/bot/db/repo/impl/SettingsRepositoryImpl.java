/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.db.repo.impl;

import com.despairs.bot.db.repo.SettingsRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author EKovtunenko
 */
//@Singleton
public class SettingsRepositoryImpl extends AbstractRepository implements SettingsRepository {

    private static final String SQL = "select %s from settings where id = :id";

    @Override
    public String getValueV(String id) throws SQLException {
        try (ResultSet rs = getValue(id, "value_v")) {
            return rs.getString(1);
        }
    }

    @Override
    public Long getValueN(String id) throws SQLException {
        try (ResultSet rs = getValue(id, "value_n")) {
            long val = rs.getLong(1);
            return rs.wasNull() ? null : val;
        }
    }

    @Override
    public Boolean getValueB(String id) throws SQLException {
        try (ResultSet rs = getValue(id, "value_b")) {
            return rs.getBoolean(1);
        }
    }

    private ResultSet getValue(String id, String targetField) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", id);
        CachedRowSet rs = select(String.format(SQL, targetField, targetField), variables);
        if (!rs.next() || rs.size() == 0) {
            throw new SQLException(String.format("Setting with ID=%s not found", id));
        }
        return rs;
    }
}
