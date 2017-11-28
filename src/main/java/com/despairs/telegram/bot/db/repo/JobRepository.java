/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.db.repo;

import com.despairs.telegram.bot.model.JobEntry;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author EKovtunenko
 */
public interface JobRepository {

    void create(Integer userId, JobEntry entry) throws SQLException;

    List<JobEntry> list(Integer userId) throws SQLException;

    List<JobEntry> list(Integer userId, String project) throws SQLException;
    
    void delete(Long id) throws SQLException;

}
