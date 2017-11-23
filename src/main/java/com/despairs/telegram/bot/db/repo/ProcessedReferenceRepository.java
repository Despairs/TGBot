/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.db.repo;

import java.sql.SQLException;

/**
 *
 * @author EKovtunenko
 */
public interface ProcessedReferenceRepository {

    void createReference(String ref, String producer) throws SQLException;
    
    void updateReference(String oldRef, String newRef, String producer) throws SQLException;

    boolean isReferenceStored(String ref, String producer) throws SQLException;
    
    String getLastReference(String producer) throws SQLException;
}
