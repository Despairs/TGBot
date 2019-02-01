package com.despairs.bot.commands.impl;

import com.despairs.bot.model.TGMessage;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class KfcCommandTest {

    @Test
    public void invoke() {
        new KfcCommand().invoke(null).forEach(System.out::println);
    }
}