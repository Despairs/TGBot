/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.db.repo.impl;

import com.despairs.telegram.bot.db.NamedVariablesStatement;
import com.despairs.telegram.bot.utils.FileUtils;
import com.sun.rowset.CachedRowSetImpl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author EKovtunenko
 */
public class AbstractRepository {

    private static final String CFG = "db.cfg";

    private static final List<String> cfg = FileUtils.readAsList(CFG);

    private static final String URL = cfg.get(0);
    private static final String USER = cfg.get(1);
    private static final String PASS = cfg.get(2);

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    protected boolean dml(String sql, Map<String, Object> variables) throws SQLException {
        try (Connection connection = getConnection()) {
            try (NamedVariablesStatement stmt = new NamedVariablesStatement(sql, variables, connection)) {
                int executedCount = stmt.executeUpdate();
                return executedCount > 0;
            }
        }
    }

    protected CachedRowSet select(String sql, Map<String, Object> variables) throws SQLException {
        try (Connection connection = getConnection()) {
            try (NamedVariablesStatement stmt = new NamedVariablesStatement(sql, variables, connection)) {
                CachedRowSet rs = new CachedRowSetImpl();
                rs.populate(stmt.executeQuery());
                return rs;
            }
        }
    }

}
