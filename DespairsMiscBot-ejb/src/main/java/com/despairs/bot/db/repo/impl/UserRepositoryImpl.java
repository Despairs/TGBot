/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.db.repo.impl;

import com.despairs.bot.model.User;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.rowset.CachedRowSet;
import com.despairs.bot.db.repo.UserRepository;
import javax.inject.Singleton;

/**
 *
 * @author EKovtunenko
 */
@Singleton
public class UserRepositoryImpl extends AbstractRepository implements UserRepository {

    private static final String UPDATE_SQL_REDMINE = "update bot_users set \"redmine_id\" = :redmineId where id = :id";
    private static final String INSERT_SQL = "insert into bot_users(id, name) "
            + "values (:id, :name)";
    private static final String EXISTS_SQL = "select 1 from bot_users where id = :id";
    private static final String EXISTS_REDMINE_SQL = "select 1 from bot_users where \"redmine_id\" = :redmine_id";
    private static final String GET_USER_SQL = "select * from bot_users where id = :id";

    @Override
    public void registerUser(Integer id, String name) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", id);
        variables.put("name", name);
        dml(INSERT_SQL, variables);
    }

    @Override
    public void updateRedmineId(Integer id, String redmineId) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", id);
        variables.put("redmine_id", redmineId);
        dml(UPDATE_SQL_REDMINE, variables);
    }

    @Override
    public boolean isUserRegistered(Integer id) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", id);
        CachedRowSet rs = select(EXISTS_SQL, variables);
        return rs.size() > 0;
    }

    @Override
    public boolean isRedmineUserRegistered(String redmineUserId) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("redmine_id", redmineUserId);
        CachedRowSet rs = select(EXISTS_REDMINE_SQL, variables);
        return rs.size() > 0;
    }

    @Override
    public User getUser(Integer id) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", id);
        CachedRowSet rs = select(GET_USER_SQL, variables);
        if (!rs.next() || rs.size() == 0) {
            System.out.println((String.format("User with id=%s not found", id)));
            return null;
        }
        User user = new User();
        user.setId(id);
        user.setName(rs.getString("name"));
        user.setRedmineId(rs.getString("redmine_id"));
        user.setIsAdmin(rs.getBoolean("is_admin"));
        return user;
    }

}
