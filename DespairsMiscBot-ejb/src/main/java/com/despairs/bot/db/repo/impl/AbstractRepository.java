/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.db.repo.impl;

import com.despairs.bot.db.NamedVariablesStatement;
import com.sun.rowset.CachedRowSetImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import ru.iflex.commons.logging.Log4jLogger;

/**
 *
 * @author EKovtunenko
 */
public class AbstractRepository {

    private static final String JNDI = "LOCAL_PG";

    protected Connection getConnection() throws SQLException {
        Connection connection = null;
        Properties prop = new Properties();
        prop.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        prop.put(Context.PROVIDER_URL, "t3://localhost:7001");
        try {
            Context ctx = new InitialContext(prop);
            DataSource dsDB = (DataSource) ctx.lookup(JNDI);
            connection = dsDB.getConnection();

            connection.setAutoCommit(true);

        } catch (Exception ex) {
            Log4jLogger.getLogger(this.getClass()).error(ex);
            throw new SQLException(ex);
        }
        return connection;
    }

    protected boolean dml(String sql, Map<String, Object> variables) throws SQLException {
        try (Connection connection = getConnection()) {
            try (NamedVariablesStatement stmt = new NamedVariablesStatement(sql, variables, connection)) {
                int executedCount = stmt.executeUpdate();
                return executedCount > 0;
            }
        }
    }

    protected CachedRowSet dmlWithGeneratedKeys(String sql, Map<String, Object> variables) throws SQLException {
        try (Connection connection = getConnection()) {
            try (NamedVariablesStatement stmt = new NamedVariablesStatement(sql, variables, connection)) {
                stmt.executeUpdate();
                CachedRowSet rs = new CachedRowSetImpl();
                rs.populate(stmt.getGeneratedKeys());
                return rs;
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
