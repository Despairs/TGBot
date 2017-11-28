/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
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
    
    public static String extractByRegExp(String source, String regexp) {
        String ret = null;
        Pattern p = Pattern.compile(regexp);
        Matcher matcher = p.matcher(source);
        if (matcher.find()) {
            ret = matcher.group(1);
        }
        return ret;
    }
}
