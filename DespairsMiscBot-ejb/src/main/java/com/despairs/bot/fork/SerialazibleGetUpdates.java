/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.fork;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.telegram.telegrambots.api.methods.updates.GetUpdates;
import ru.iflex.commons.logging.Log4jLogger;

/**
 *
 * @author EKovtunenko
 */
public class SerialazibleGetUpdates extends GetUpdates {

    @Override
    public String toString() {
        String ret = null;
        try {
            ret = OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            Log4jLogger.getLogger(this.getClass()).error(ex);
        }
        return ret;
    }
     
}
