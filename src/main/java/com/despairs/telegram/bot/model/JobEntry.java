/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author EKovtunenko
 */
public class JobEntry {

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    private Long id;
    private String project;
    private Double duration;
    private String comment;
    private Date timestamp;

    public JobEntry(Long id, String project, Double duration, String comment, Date timestamp) {
        this.id = id;
        this.project = project;
        this.duration = duration;
        this.comment = comment;
        this.timestamp = timestamp;
    }
    
    public JobEntry(String project, Double duration, String comment, Date timestamp) {
        this.project = project;
        this.duration = duration;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public JobEntry(String project, Double duration, Date timestamp) {
        this.project = project;
        this.duration = duration;
        this.timestamp = timestamp;
    }

    public JobEntry(String project, Double duration) {
        this.project = project;
        this.duration = duration;
    }

    public JobEntry(String project, Double duration, String comment) {
        this.project = project;
        this.duration = duration;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDateAsString() {
        return new SimpleDateFormat(DATE_PATTERN).format(timestamp);
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.id);
        hash = 83 * hash + Objects.hashCode(this.project);
        hash = 83 * hash + Objects.hashCode(this.duration);
        hash = 83 * hash + Objects.hashCode(this.comment);
        hash = 83 * hash + Objects.hashCode(this.timestamp);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JobEntry other = (JobEntry) obj;
        if (!Objects.equals(this.project, other.project)) {
            return false;
        }
        if (!Objects.equals(this.comment, other.comment)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.duration, other.duration)) {
            return false;
        }
        if (!Objects.equals(this.timestamp, other.timestamp)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "JobEntry{" + "id=" + id + ", project=" + project + ", duration=" + duration + ", comment=" + comment + ", timestamp=" + timestamp + '}';
    }

}
