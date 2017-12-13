/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.db.repo;

import com.despairs.bot.model.JobEntry;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author EKovtunenko
 */
public interface JobRepository {

    Long create(Integer userId, JobEntry entry) throws SQLException;

    List<JobEntry> list(Integer userId) throws SQLException;

    List<JobEntry> listFromLastPayment(Integer userId, String project) throws SQLException;

    JobEntry get(Integer userId, Long id) throws SQLException;

    List<JobEntry> list(Integer userId, String project) throws SQLException;

    boolean delete(Integer userId, Long id) throws SQLException;

    boolean update(Integer userId, JobEntry entry) throws SQLException;

    Long setLastPaymentDate(String project) throws SQLException;

    Long setLastPaymentDate(String project, Date date) throws SQLException;

    Date getLastPaymentDate(String project) throws SQLException;

    List<String> getProjects(Integer userId) throws SQLException;
}
