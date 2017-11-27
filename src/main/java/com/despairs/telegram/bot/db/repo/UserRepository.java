/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.db.repo;

import com.despairs.telegram.bot.model.User;
import java.sql.SQLException;

/**
 *
 * @author EKovtunenko
 */
public interface UserRepository {

    void registerUser(Integer id, String name, String redmineId) throws SQLException;
    
    void registerUser(Integer id, String name) throws SQLException;

    boolean isUserRegistered(Integer id) throws SQLException;
    
    boolean isRedmineUserRegistered(String redmineUserId) throws SQLException;

    User getUser(Integer id) throws SQLException;
}
