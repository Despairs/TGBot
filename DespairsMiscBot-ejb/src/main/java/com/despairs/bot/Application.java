/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot;

import com.despairs.bot.tg.TelegramUpdateProducer;
import java.util.List;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.objects.Update;
import ru.iflex.commons.logging.Log4jLogger;

/**
 *
 * @author EKovtunenko
 */
@Singleton
@LocalBean
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Lock(LockType.READ)
public class Application {

    private Logger logger = Log4jLogger.getLogger(Application.class);

    @EJB
    private TelegramUpdateProducer updateProducer;

    @Inject
    private Bot bot;

    @Schedule(hour = "*", minute = "*", second = "*", persistent = false)
    public void doAction() {
        Log4jLogger.pushThread();
        try {
            List<Update> updates = updateProducer.produce();
            bot.onUpdatesReceived(updates);
        } catch (Exception ex) {
            logger.error("Application job lifecycle error", ex);
        }
    }

}
