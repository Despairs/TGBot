/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.db.repo.impl;

import com.despairs.telegram.bot.model.User;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.rowset.CachedRowSet;
import com.despairs.telegram.bot.db.repo.UserRepository;

/**
 *
 * @author EKovtunenko
 */
public class UserRepositoryImpl extends AbstractRepository implements UserRepository {

    private static final String INSERT_SQL = "insert into bot_users(\"redmineId\", id, name) "
            + "values (:redmineId, :id, :name)";
    private static final String EXISTS_SQL = "select 1 from bot_users where id = :id";
    private static final String GET_USER_SQL = "select * from bot_users where id = :id";

    private static final UserRepository instance = new UserRepositoryImpl();

    public static UserRepository getInstance() {
        return instance;
    }

    @Override
    public void registerUser(String id, String name, String redmineId) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", id);
        variables.put("name", name);
        variables.put("redmineId", redmineId);
        insertOrUpdate(INSERT_SQL, variables);
    }

    @Override
    public boolean isUserRegistered(String id) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", id);
        CachedRowSet rs = select(EXISTS_SQL, variables);
        return rs.size() > 0;
    }

    @Override
    public User getUser(String id) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", id);
        CachedRowSet rs = select(GET_USER_SQL, variables);
        if (!rs.next() || rs.size() == 0) {
            throw new SQLException(String.format("User with id=%s not found", id));
        }
        User user = new User();
        user.setId(id);
        user.setName(rs.getString("name"));
        user.setRedmineId(rs.getString("redmineId"));
        return user;
    }

}