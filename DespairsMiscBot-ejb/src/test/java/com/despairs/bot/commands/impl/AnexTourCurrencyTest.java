package com.despairs.bot.commands.impl;

import com.despairs.bot.clients.AnexTour;
import org.junit.Test;

import java.time.LocalDate;

public class AnexTourCurrencyTest {

    @Test
    public void invoke() {
        System.out.println(AnexTour.API.getCurrency(LocalDate.now()));
        System.out.println(AnexTour.API.getCurrency(LocalDate.now().minusDays(1)));
    }
}