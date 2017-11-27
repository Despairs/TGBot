/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.db.repo.impl;

import com.despairs.telegram.bot.db.repo.JobRepository;
import com.despairs.telegram.bot.model.JobEntry;
import com.despairs.telegram.bot.model.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author EKovtunenko
 */
public class JobRepositoryImpl extends AbstractRepository implements JobRepository {

    private static final String INSERT_SQL_DURATION = "insert into job(user_id, project, time_entry) "
            + "values (:user_id, :project, :time_entry)";
    private static final String INSERT_SQL_ACTION = "insert into job(user_id, project, action) "
            + "values (:user_id, :project, :action)";
    private static final String SELECT_BY_PROJECT_AND_DAYS_SQL = "select * from job where user_id = :user_id and project = :project and timestamp > now() - (:daysAgo || ' days') :: INTERVAL";
    private static final String SELECT_BY_PROJECT_SQL = "select * from job where user_id = :user_id and project = :project";
    private static final String SELECT_ALL_SQL = "select * from job where user_id = :user_id";
    private static final String SELECT_BY_DAYS_SQL = "select * from job where user_id = :user_id and timestamp > now() - (:daysAgo || ' days') :: INTERVAL";
    
    
    private static final JobRepository instance = new JobRepositoryImpl();

    public static JobRepository getInstance() {
        return instance;
    }

    @Override
    public void createEntry(Integer userId, String project, String action) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user_id", userId);
        variables.put("project", project);
        variables.put("action", action);
        insertOrUpdate(INSERT_SQL_ACTION, variables);
    }

    @Override
    public void createEntry(Integer userId, String project, Double duration) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user_id", userId);
        variables.put("project", project);
        variables.put("time_entry", duration);
        insertOrUpdate(INSERT_SQL_DURATION, variables);
    }

    @Override
    public List<JobEntry> getEntries(Integer userId) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user_id", userId);
        CachedRowSet rs = select(SELECT_ALL_SQL, variables);
        List<JobEntry> ret = new ArrayList();
        while (rs.next()) {
            JobEntry entry = new JobEntry();
            entry.setProject(rs.getString("project"));
            entry.setDuration(rs.getDouble("time_entry"));
            entry.setTimestamp(rs.getTimestamp("timestamp"));
            ret.add(entry);
        }
        return ret;
    }

    @Override
    public List<JobEntry> getEntries(Integer userId, String project) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user_id", userId);
        variables.put("project", project);
        CachedRowSet rs = select(SELECT_BY_PROJECT_SQL, variables);
        List<JobEntry> ret = new ArrayList();
        while (rs.next()) {
            JobEntry entry = new JobEntry();
            entry.setProject(rs.getString("project"));
            entry.setDuration(rs.getDouble("time_entry"));
            entry.setTimestamp(rs.getDate("timestamp"));
            ret.add(entry);
        }
        return ret;
    }

    @Override
    public List<JobEntry> getEntries(Integer userId, String project, int daysAgo) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user_id", userId);
        variables.put("project", project);
        variables.put("daysAgo", daysAgo);
        CachedRowSet rs = select(SELECT_BY_PROJECT_AND_DAYS_SQL, variables);
        List<JobEntry> ret = new ArrayList();
        while (rs.next()) {
            JobEntry entry = new JobEntry();
            entry.setProject(rs.getString("project"));
            entry.setDuration(rs.getDouble("time_entry"));
            entry.setTimestamp(rs.getDate("timestamp"));
            ret.add(entry);
        }
        return ret;
    }

    @Override
    public List<JobEntry> getEntries(Integer userId, int daysAgo) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user_id", userId);
        variables.put("daysAgo", daysAgo);
        CachedRowSet rs = select(SELECT_BY_DAYS_SQL, variables);
        List<JobEntry> ret = new ArrayList();
        while (rs.next()) {
            JobEntry entry = new JobEntry();
            entry.setProject(rs.getString("project"));
            entry.setDuration(rs.getDouble("time_entry"));
            entry.setTimestamp(rs.getDate("timestamp"));
            ret.add(entry);
        }
        return ret;
    }

}
