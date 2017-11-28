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
    private static final String SELECT_BY_PROJECT_SQL = "select * from job where user_id = :user_id and project = :project order by timestamp desc";
    private static final String SELECT_ALL_SQL = "select * from job where user_id = :user_id order by timestamp desc";
    private static final String DELETE = "delete from job where id = :id";

    private static final JobRepository instance = new JobRepositoryImpl();

    public static JobRepository getInstance() {
        return instance;
    }

    @Override
    public void create(Integer userId, JobEntry entry) throws SQLException {
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
        dml(sql, variables);
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
        CachedRowSet rs = select(SELECT_BY_PROJECT_SQL, variables);
        List<JobEntry> ret = new ArrayList();
        while (rs.next()) {
            ret.add(new JobEntry(rs.getLong("id"), rs.getString("project"), rs.getDouble("time_entry"), rs.getString("comment"), rs.getDate("timestamp")));
        }
        return ret;
    }

    @Override
    public void delete(Long id) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", id);
        dml(DELETE, variables);
    }

}
