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

public class QueueSelector {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueueSelector.class);

	private final String SELECTOR_ID;
	private MessageConsumer consumer;
	private Connection connection;
	private Session session;

	private ConnectionFactory inVMConnectionFactory;

	private Queue selectorQueue;

	public QueueSelector(int i) {
		SELECTOR_ID = "SELECTOR" + i;
		try {
			LOGGER.info("Register selector {} to queue", SELECTOR_ID);
			InitialContext ic = new InitialContext();
			inVMConnectionFactory = (ConnectionFactory) ic.lookup(QueueProps.QUEUE_CON_FACTORY);
			connection = inVMConnectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			selectorQueue = (Queue) ic.lookup(QueueProps.SELECTOR_QUEUE);
			
			String messageSelector = String.format("%s = '%s'", QueueProps.SELECTOR_ID_KEY, SELECTOR_ID);
        	consumer = session.createConsumer(selectorQueue, messageSelector);
            consumer.setMessageListener(new MessageListener() {

				public void onMessage(Message message) {
					processMessage(message);
				}
            });
		} catch (Exception e) {
			LOGGER.error("Error creating selector " + SELECTOR_ID, e);
		}
	}
	
	public void processMessage(Message message) {
		LOGGER.info("Processing message for selector " + SELECTOR_ID);
		try {
			if (message instanceof TextMessage) {
				String text = ((TextMessage) message).getText();
				LOGGER.info("Processing message " + text);
			}
			Thread.sleep(100);
		} catch (Exception e) {
			LOGGER.error("Error while sleeping", e);
		}
	}
	
	public void resume() {
        try {
            LOGGER.info("Start/Resume the message listener {}", SELECTOR_ID);
            connection.start();
        } catch (Exception ex) {
            LOGGER.error("exception in start/resume", ex);
        }
    }
	
	public void pause() {
		try {
            LOGGER.info("Pause the message listener {}", SELECTOR_ID);
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
}
