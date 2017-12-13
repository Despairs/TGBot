/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.iflex.commons.logging.Log4jLogger;

/**
 * Класс, позволяющий выполнять SQL с именованными переменными.
 * Пример: select * from table where param1 = :val and param2 = :new_val
 *
 * Ограничения: передаваемые параметры должны быть простыми Java-объектами
 * (String, Integer, Long и тд)
 *
 * @author EKovtunenko
 */
public class NamedVariablesStatement implements AutoCloseable {

    private org.apache.logging.log4j.Logger logger = Log4jLogger.getLogger(NamedVariablesStatement.class);
    
    private static final Pattern VARIABLE_PATTERN = Pattern.compile(":(\\w+)");
    private static final String MARKER = "?";

    private String sql;
    private PreparedStatement statement;
    private final Map<Integer, Object> indexedVariables = new HashMap<>();

    public NamedVariablesStatement(String sql, Map<String, Object> variables, Connection connection) throws SQLException {
        this.sql = sql;
        parseSqlAndIndexVariables(variables);
        buildStatement(connection);
    }

    private void parseSqlAndIndexVariables(Map<String, Object> variables) {
        Matcher m = VARIABLE_PATTERN.matcher(sql);
        int index = 1;
        while (m.find()) {
            String fullVariable = m.group();
            String cuttedVariable = m.group(1);
            Object variableValue = variables.get(cuttedVariable);
            if (variableValue != null) {
                if (variableValue instanceof List) {
                    String markerList = "";
                    for (Object o : (List) variableValue) {
                        markerList += MARKER + ",";
                        indexedVariables.put(index++, o);
                    }
                    markerList = markerList.substring(0, markerList.length() - 1);
                    sql = sql.replace(fullVariable, markerList);
                } else {
                    sql = sql.replace(fullVariable, MARKER);
                    indexedVariables.put(index++, variableValue);
                }
            }
        }
        logger.trace(sql);
    }

    private void buildStatement(Connection connection) throws SQLException {
        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (Map.Entry<Integer, Object> entry : indexedVariables.entrySet()) {
            if (entry.getValue() instanceof Date) {
                statement.setObject(entry.getKey(), entry.getValue(), Types.DATE);
            } else {
                statement.setObject(entry.getKey(), entry.getValue());
            }
        }
    }

    public int executeUpdate() throws SQLException {
        return statement.executeUpdate();
    }

    public ResultSet executeQuery() throws SQLException {
        return statement.executeQuery();
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return statement.getGeneratedKeys();
    }

    @Override
    public void close() throws SQLException {
        if (statement != null && !statement.isClosed()) {
            statement.close();
        }
    }

}
