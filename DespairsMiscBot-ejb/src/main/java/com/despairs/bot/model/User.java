/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.model;

import lombok.Data;

import javax.enterprise.context.RequestScoped;

/**
 * @author EKovtunenko
 */
@Data
@RequestScoped
public class User {

    private Integer id;
    private String name;
    private String redmineId;
    private boolean isAdmin;

}
