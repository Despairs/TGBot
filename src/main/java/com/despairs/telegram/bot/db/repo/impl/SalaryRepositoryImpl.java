/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.db.repo.impl;

import com.despairs.telegram.bot.db.repo.SalaryRepository;
import com.despairs.telegram.bot.model.SalaryEntry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author EKovtunenko
 */
public class SalaryRepositoryImpl extends AbstractRepository implements SalaryRepository {

    private static final String INSERT = "insert into salary (period, amount) values (:period, :amount)";
    private static final String INSERT_WITH_COMMENT = "insert into salary (period, amount, comment) values (:period, :amount, :comment)";

    private static final String SELECT = "select * from salary where period = :period";
    private static final String SELECT_SUM_BY_PERIOD = "select sum(amount) as amount from salary where period = :period group by period";
    
    private static final String DELETE = "delete from salary where id = :id";

    private static final SalaryRepository instance = new SalaryRepositoryImpl();

    public static SalaryRepository getInstance() {
        return instance;
    }

    @Override
    public Long create(SalaryEntry entry) throws SQLException {
        String sql = INSERT;
        Map<String, Object> variables = new HashMap<>();
        variables.put("period", entry.getPeriod());
        variables.put("amount", entry.getAmount());
        String comment = entry.getComment();
        if (comment != null) {
            variables.put("comment", comment);
            sql = INSERT_WITH_COMMENT;
        }
        CachedRowSet rs = dmlWithGeneratedKeys(sql, variables);
        if (!rs.next()) {
            throw new SQLException("Can't get generated entry id");
        }
        return rs.getLong(1);
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", id);
        return dml(DELETE, variables);
    }

    @Override
    public SalaryEntry get(Long id) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SalaryEntry> list(String period) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("period", period);
        CachedRowSet rs = select(SELECT, variables);
        List<SalaryEntry> ret = new ArrayList();
        while (rs.next()) {
            ret.add(new SalaryEntry(rs.getLong("id"), rs.getDouble("amount"), rs.getString("period"), rs.getDate("timestamp"), rs.getString("comment")));
        }
        return ret;
    }

    @Override
    public Double sumByPeriod(String period) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("period", period);
        CachedRowSet rs = select(SELECT_SUM_BY_PERIOD, variables);
        Double ret = null;
        while (rs.next()) {
            ret = rs.getDouble("amount");
        }
        return ret;
    }

}
