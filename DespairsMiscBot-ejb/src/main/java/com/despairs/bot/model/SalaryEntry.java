/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.model;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.despairs.bot.model.JobEntry.DATE_PATTERN;

/**
 * @author EKovtunenko
 */
@Data
public class SalaryEntry {

    private Long id;
    private Double amount;
    private String period;
    private Date timestamp;
    private String comment;

    public SalaryEntry(Long id, Double amount, String period, Date timestamp, String comment) {
        this.id = id;
        this.amount = amount;
        this.period = period;
        this.timestamp = timestamp;
        this.comment = comment;
    }

    public SalaryEntry(Double amount, String period, String comment) {
        this.amount = amount;
        this.period = period;
        this.comment = comment;
    }

    public String getDateAsString() {
        return new SimpleDateFormat(DATE_PATTERN).format(timestamp);
    }

}
