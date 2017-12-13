/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.db.repo;

import com.despairs.bot.model.SalaryEntry;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author EKovtunenko
 */
public interface SalaryRepository {

    Long create(SalaryEntry entry) throws SQLException;

    boolean delete(Long id) throws SQLException;

    SalaryEntry get(Long id) throws SQLException;

    List<SalaryEntry> list(String period) throws SQLException;
    
    Double sumByPeriod(String period) throws SQLException;

}
