/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.db.repo.impl;

import com.despairs.telegram.bot.db.repo.JobRepository;
import com.despairs.telegram.bot.model.JobEntry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author EKovtunenko
 */
public class JobRepositoryImpl extends AbstractRepository implements JobRepository {

    private static final String INSERT = "insert into job(user_id, project, time_entry) "
            + "values (:user_id, :project, :time_entry)";
    private static final String INSERT_WITH_COMMENT = "insert into job(user_id, project, time_entry, comment) "
            + "values (:user_id, :project, :time_entry, :comment)";
    private static final String INSERT_WITH_DATE = "insert into job(user_id, project, time_entry, timestamp) "
            + "values (:user_id, :project, :time_entry, :timestamp)";
    private static final String INSERT_WITH_DATE_AND_COMMENT = "insert into job(user_id, project, time_entry, timestamp, comment) "
            + "values (:user_id, :project, :time_entry, :timestamp, :comment)";

    private static final String SELECT_ALL_BY_PROJECT_SQL = "select * from job where user_id = :user_id and project = :project order by timestamp desc";
    private static final String SELECT_ALL_SQL = "select * from job where user_id = :user_id order by timestamp desc";

    private static final String SELECT_ALL_BY_PROJECT_FROM_LAST_PAYMENT = "SELECT j.* FROM job j "
            + "LEFT OUTER JOIN job_payments jp ON jp.project = j.project "
            + "WHERE j.user_id = :user_id AND j.project = :project "
            + "AND (jp.last_payment_date IS NULL OR j.\"timestamp\" >= jp.last_payment_date) "
            + "ORDER BY TIMESTAMP DESC";
    private static final String SELECT_ALL_SQL_FROM_LAST_PAYMENT = "SELECT j.* FROM job j "
            + "LEFT OUTER JOIN job_payments jp ON jp.project = j.project "
            + "WHERE j.user_id = :user_id "
            + "AND (jp.last_payment_date IS NULL OR j.timestamp >= jp.last_payment_date) "
            + "ORDER BY TIMESTAMP DESC";

    private static final String SELECT = "select * from job where user_id = :user_id and id = :id";

    private static final String SELECT_USER_PROJECTS = "select distinct project from job where user_id = :user_id";

    private static final String DELETE = "delete from job where user_id = :user_id and id = :id";
    private static final String UPDATE = "update job set %s where user_id = :user_id and id = :id";

    private static final JobRepository instance = new JobRepositoryImpl();

    public static JobRepository getInstance() {
        return instance;
    }

    @Override
    public Long create(Integer userId, JobEntry entry) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user_id", userId);
        variables.put("project", entry.getProject());
        variables.put("time_entry", entry.getDuration());
        Date timestamp = entry.getTimestamp();
        String sql = INSERT;
        if (timestamp != null) {
            variables.put("timestamp", timestamp);
            sql = INSERT_WITH_DATE;
        }
        String comment = entry.getComment();
        if (comment != null) {
            variables.put("comment", comment);
            sql = timestamp == null ? INSERT_WITH_COMMENT : INSERT_WITH_DATE_AND_COMMENT;
        }
        CachedRowSet rs = dmlWithGeneratedKeys(sql, variables);
        if (!rs.next()) {
            throw new SQLException("Can't get generated entry id");
        }
        return rs.getLong(1);
    }

    @Override
    public List<JobEntry> list(Integer userId) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user_id", userId);
        CachedRowSet rs = select(SELECT_ALL_SQL, variables);
        List<JobEntry> ret = new ArrayList();
        while (rs.next()) {
            ret.add(new JobEntry(rs.getLong("id"), rs.getString("project"), rs.getDouble("time_entry"), rs.getString("comment"), rs.getDate("timestamp")));
        }
        return ret;
    }

    @Override
    public List<JobEntry> list(Integer userId, String project) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user_id", userId);
        variables.put("project", project);
        CachedRowSet rs = select(SELECT_ALL_BY_PROJECT_SQL, variables);
        List<JobEntry> ret = new ArrayList();
        while (rs.next()) {
            ret.add(new JobEntry(rs.getLong("id"), rs.getString("project"), rs.getDouble("time_entry"), rs.getString("comment"), rs.getDate("timestamp")));
        }
        return ret;
    }

    @Override
    public boolean delete(Integer userId, Long id) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user_id", userId);
        variables.put("id", id);
        return dml(DELETE, variables);
    }

    @Override
    public boolean update(Integer userId, JobEntry entry) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user_id", userId);
        variables.put("id", entry.getId());
        StringBuilder sb = new StringBuilder();
        String comment = entry.getComment();
        if (comment != null) {
            variables.put("comment", comment);
            sb.append("comment = :comment, ");
        }
        Double duration = entry.getDuration();
        if (duration != null) {
            variables.put("time_entry", duration);
            sb.append("time_entry = :time_entry, ");
        }
        Date timestamp = entry.getTimestamp();
        if (timestamp != null) {
            variables.put("timestamp", timestamp);
            sb.append("timestamp = :timestamp, ");
        }
        int length = sb.length();
        if (length != 0) {
            sb.deleteCharAt(length - 2);
        }
        return dml(String.format(UPDATE, sb.toString()), variables);
    }

    @Override
    public JobEntry get(Integer userId, Long id) throws SQLException {
        JobEntry ret = null;
        Map<String, Object> variables = new HashMap<>();
        variables.put("user_id", userId);
        variables.put("id", id);
        CachedRowSet rs = select(SELECT, variables);
        if (rs.next()) {
            ret = new JobEntry(rs.getLong("id"), rs.getString("project"), rs.getDouble("time_entry"), rs.getString("comment"), rs.getDate("timestamp"));
        }
        return ret;
    }

    @Override
    public Long setLastPaymentDate(String project) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long setLastPaymentDate(String project, Date date) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Date getLastPaymentDate(String project) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getProjects(Integer userId) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user_id", userId);
        CachedRowSet rs = select(SELECT_USER_PROJECTS, variables);
        List<String> ret = new ArrayList();
        while (rs.next()) {
            ret.add(rs.getString("project"));
        }
        return ret;
    }

    @Override
    public List<JobEntry> listFromLastPayment(Integer userId, String project) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user_id", userId);
        variables.put("project", project);
        CachedRowSet rs = select(SELECT_ALL_BY_PROJECT_FROM_LAST_PAYMENT, variables);
        List<JobEntry> ret = new ArrayList();
        while (rs.next()) {
            ret.add(new JobEntry(rs.getLong("id"), rs.getString("project"), rs.getDouble("time_entry"), rs.getString("comment"), rs.getDate("timestamp")));
        }
        return ret;
    }

}
