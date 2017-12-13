/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author EKovtunenko
 */
public class FileUtils {

    public static String read(String name) {
        String ret = "";
        return readAsList(name).stream().reduce(ret, String::concat);
    }

    public static List<String> readAsList(String name) {
        List<String> ret = Collections.EMPTY_LIST;
        Charset charset = Charset.forName("UTF-8");
        try {
            ret = Files.readAllLines(Paths.get(name), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void write(String s, String name) {
        write(s, name, true);

    }

    public static void write(String s, String name, boolean append) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(name), append));) {
            writer.println(s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
