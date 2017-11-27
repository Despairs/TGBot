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

    void createEntry(Integer userId, String project, Double duration) throws SQLException;
    
    void createEntry(Integer userId, String project, Double duration, Date timestamp) throws SQLException;

    List<JobEntry> getEntries(Integer userId) throws SQLException;

    List<JobEntry> getEntries(Integer userId, String project) throws SQLException;

    List<JobEntry> getEntries(Integer userId, int daysAgo) throws SQLException;

    List<JobEntry> getEntries(Integer userId, String project, int daysAgo) throws SQLException;

}
