/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.db.repo.impl;

import com.despairs.bot.db.repo.ProcessedReferenceRepository;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author EKovtunenko
 */
@Singleton
public class ProcessedReferenceRepositoryImpl extends AbstractRepository implements ProcessedReferenceRepository {

    private static final String INSERT_SQL = "insert into processed_reference(producer, reference) values (:producer, :reference)";
    private static final String UPDATE_SQL = "update processed_reference set reference = :new_reference, timestamp = now() where producer = :producer and reference = :old_reference";
    private static final String EXISTS_SQL = "select 1 from processed_reference where producer = :producer and reference = :reference";
    private static final String LAST_REFERENCE_SQL = "select reference from processed_reference where producer = :producer order by id, timestamp desc limit 1";

    @Override
    public void createReference(String ref, String producer) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("producer", producer);
        variables.put("reference", ref);
        dml(INSERT_SQL, variables);
    }

    @Override
    public boolean isReferenceStored(String ref, String producer) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("producer", producer);
        variables.put("reference", ref);
        CachedRowSet rs = select(EXISTS_SQL, variables);
        return rs.size() > 0;
    }

    @Override
    public String getLastReference(String producer) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("producer", producer);
        CachedRowSet rs = select(LAST_REFERENCE_SQL, variables);
        if (rs.next()) {
            return rs.getString("reference");
        }
        return null;
    }

    @Override
    public void updateReference(String oldRef, String newRef, String producer) throws SQLException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("producer", producer);
        variables.put("old_reference", oldRef);
        variables.put("new_reference", newRef);
        dml(UPDATE_SQL, variables);
    }

}
