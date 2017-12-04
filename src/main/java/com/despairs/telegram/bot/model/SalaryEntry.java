/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.model;

import static com.despairs.telegram.bot.model.JobEntry.DATE_PATTERN;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author EKovtunenko
 */
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

    public SalaryEntry(Double amount, String period) {
        this.amount = amount;
        this.period = period;
    }

    public SalaryEntry(Double amount, String period, String comment) {
        this.amount = amount;
        this.period = period;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDateAsString() {
        return new SimpleDateFormat(DATE_PATTERN).format(timestamp);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.amount);
        hash = 41 * hash + Objects.hashCode(this.period);
        hash = 41 * hash + Objects.hashCode(this.timestamp);
        hash = 41 * hash + Objects.hashCode(this.comment);
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
        final SalaryEntry other = (SalaryEntry) obj;
        if (!Objects.equals(this.comment, other.comment)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.amount, other.amount)) {
            return false;
        }
        if (!Objects.equals(this.period, other.period)) {
            return false;
        }
        if (!Objects.equals(this.timestamp, other.timestamp)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SalaryEntry{" + "id=" + id + ", amount=" + amount + ", period=" + period + ", timestamp=" + timestamp + ", comment=" + comment + '}';
    }

}
