/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.db.repo;

import java.sql.SQLException;

/**
 * @author EKovtunenko
 */
public interface ProcessedReferenceRepository {

    void createReference(String ref, String producer);

    void updateReference(String oldRef, String newRef, String producer) throws SQLException;

    boolean isReferenceStored(String ref, String producer);

    String getLastReference(String producer) throws SQLException;
}
