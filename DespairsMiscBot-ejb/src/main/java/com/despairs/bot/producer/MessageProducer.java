/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.producer;

import com.despairs.bot.model.TGMessage;
import java.util.List;

/**
 *
 * @author EKovtunenko
 */
public interface MessageProducer {

    List<TGMessage> produce() throws Exception;
}
