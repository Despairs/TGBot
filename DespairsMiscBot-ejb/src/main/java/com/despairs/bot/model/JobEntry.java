/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.model;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author EKovtunenko
 */
@Data
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

    public JobEntry(Long id, Double duration, String comment, Date timestamp) {
        this.id = id;
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

    public JobEntry(String project, Double duration, String comment) {
        this.project = project;
        this.duration = duration;
        this.comment = comment;
    }

    public String getDateAsString() {
        return new SimpleDateFormat(DATE_PATTERN).format(timestamp);
    }

}
