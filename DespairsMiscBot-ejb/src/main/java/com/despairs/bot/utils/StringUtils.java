/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.utils;

/**
 * @author EKovtunenko
 */
public class StringUtils {

    private static final Integer MAX_LENGTH = 3900;

    public static String normalize(String s) {
        if (s == null) {
            return s;
        }
        String ret = s.replaceAll("&", "amp&;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        if (ret.length() > MAX_LENGTH) {
            ret = ret.substring(0, MAX_LENGTH).concat("...");
        }
        return ret;
    }


    public static boolean isNullOrEmpty(String s) {
        return s == null || "null".equals(s) || s.isEmpty();
    }
}
