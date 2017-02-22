/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.examples.queue;

import java.lang.reflect.Method;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author domov
 */
public class GroupConsumer implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueSelector.class);

    private final String GROUP_ID;
    private MessageConsumer consumer;
    private Connection connection;
    private Session session;

    private ConnectionFactory inVMConnectionFactory;

    private Queue mdbQueue;

    public GroupConsumer(int i) {
        GROUP_ID = "GroupConsumer" + i;
        
        try {
            LOGGER.info("Register group consumer {} to queue", GROUP_ID);
            InitialContext ic = new InitialContext();
            inVMConnectionFactory = (ConnectionFactory) ic.lookup(QueueProps.QUEUE_CON_FACTORY);
            connection = inVMConnectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            mdbQueue = (Queue) ic.lookup(QueueProps.MDB_QUEUE);

            consumer = session.createConsumer(mdbQueue);
            consumer.setMessageListener(this);
        } catch (Exception e) {
            LOGGER.error("Error creating group consumer " + GROUP_ID, e);
        }
    }

    public void resume() {
        try {
            LOGGER.info("Start/Resume the message listener {}", GROUP_ID);
            connection.start();
        } catch (Exception ex) {
            LOGGER.error("exception in start/resume", ex);
        }
    }

    public void pause() {
        try {
            LOGGER.info("Pause the message listener {}", GROUP_ID);
            connection.stop();
        } catch (Exception ex) {
            LOGGER.error("exception in pause", ex);
        }
    }

    public void stop() {
        close(consumer);
        close(session);
        close(connection);
    }

    void close(Object closeable) {
        try {
            if (closeable != null) {
                Method method = closeable.getClass().getMethod("close");
                if (method != null) {
                    method.invoke(closeable, new Object[0]);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("JMS Cleanup failed: ", ex);
        }
    }
    
    @Override
    public void onMessage(Message message) {
        LOGGER.info("Processing message with consumer " + GROUP_ID);
        try {
            if (message instanceof TextMessage) {
                String text = ((TextMessage) message).getText();
                String group = message.getStringProperty(QueueProps.JMS_GROUP_KEY);
                LOGGER.info("Processing message " + text + " for group " + group);
            }
            Thread.sleep(1000);
        } catch (Exception e) {
            LOGGER.error("Error while sleeping", e);
        }
    }
}
