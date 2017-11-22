/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.model;

import java.util.Objects;

/**
 *
 * @author EKovtunenko
 */
public class User {

    private String name;
    private String id;
    private String redmineId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRedmineId() {
        return redmineId;
    }

    public void setRedmineId(String redmineId) {
        this.redmineId = redmineId;
    }

    @Override
    public String toString() {
        return "User{" + "name=" + name + ", id=" + id + ", redmineId=" + redmineId + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.name);
        hash = 43 * hash + Objects.hashCode(this.id);
        hash = 43 * hash + Objects.hashCode(this.redmineId);
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
        final User other = (User) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.redmineId, other.redmineId)) {
            return false;
        }
        return true;
    }

}
