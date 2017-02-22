package com.examples.rs;

import com.examples.queue.GroupConsumer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.examples.queue.QueueProps;
import com.examples.queue.QueueSelector;
import javax.jms.Message;

@Stateless
@Local(QueueService.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class QueueServiceBean implements QueueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueServiceBean.class);

    @Resource(mappedName = QueueProps.QUEUE_CON_FACTORY)
    private ConnectionFactory inVMConnectionFactory;

    @Resource(mappedName = QueueProps.SELECTOR_QUEUE)
    private Queue selectorQueue;

    @Resource(mappedName = QueueProps.MDB_QUEUE)
    private Queue mdbQueue;

    private MessageProducer producer;

    private List<QueueSelector> selectorList = new ArrayList<QueueSelector>();

    public void ping() {
        LOGGER.info("PING on Queue service!");
    }

    public void doTest() {
        generateMessagesToSelectorQueueSpecial();
        generateMessagesToSelectorQueue();
        registerSelectors();
        startSelectors();
    }

    public void generateMessagesToMDBQueue() {
        try {
            LOGGER.info("Generating messages for the mdbQueue");
            Connection connection = inVMConnectionFactory.createConnection();
            Session session;
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(mdbQueue);
            connection.start();
            // generate messages for three groups
            for (int j = 0; j < 3; j++) {
                for (int i = 0; i < 1000; i++) {
                    TextMessage msg = session.createTextMessage("My Command message " + j + i);
                    msg.setStringProperty(QueueProps.JMS_GROUP_KEY, "Group" + j);
                    producer.send(msg);
                }
            }
            connection.close();
            LOGGER.info("Generation of messages done");
        } catch (JMSException e) {
            LOGGER.error("Failed to geenrate messages ", e);
        }
    }

    public void startListenersForMDBQueue() {
        List<GroupConsumer> consumerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            consumerList.add(new GroupConsumer(i));
        }
        
        for (int i = 0; i < 3; i++) {
            consumerList.get(i).resume();
        }
        
    }
    
    public void generateMessagesToSelectorQueueSpecial() {
        try {
            LOGGER.info("Generating messages for the selectorQueue special");
            Connection connection = inVMConnectionFactory.createConnection();
            Session session;
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(selectorQueue);
            connection.start();
            // generate messages for a special selector
            String selectorID = QueueProps.SELECTOR_PREFIX + 666;
            for (int i = 0; i < 60000; i++) {
                TextMessage msg = session.createTextMessage("My Command message " + 666 + " / " + i);
                msg.setStringProperty(QueueProps.SELECTOR_ID_KEY, selectorID);
                producer.send(msg);
                if ((i % 1000) == 0) {
                    LOGGER.debug("1000 special messages created");
                }
            }
            connection.close();
            LOGGER.info("Generation of messages done");
        } catch (JMSException e) {
            LOGGER.error("Failed to geenrate messages ", e);
        }
    }

    public void generateMessagesToSelectorQueue() {
        try {
            LOGGER.info("Generating messages for the selectorQueue");
            Connection connection = inVMConnectionFactory.createConnection();
            Session session;
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(selectorQueue);
            connection.start();
            // generate messages for 60 selectors
            for (int i = 0; i < 60; i++) {
                String selectorID = QueueProps.SELECTOR_PREFIX + i;
                for (int j = 0; j < 1000; j++) {
                    TextMessage msg = session.createTextMessage("My Command message " + i + " / " + j);
                    msg.setStringProperty(QueueProps.SELECTOR_ID_KEY, selectorID);

                    producer.send(msg);
                }
                LOGGER.info("Messages generated for selector {}", selectorID);
            }
            connection.close();
            LOGGER.info("Generation of messages done");
        } catch (JMSException e) {
            LOGGER.error("Failed to geenrate messages ", e);
        }
    }

    public void registerSelectors() {
        LOGGER.info("Register selectors to queue");
        selectorList.clear();
        for (int i = 0; i < 60; i++) {
            QueueSelector selector = new QueueSelector(i);
            selectorList.add(selector);
        }

        LOGGER.info("Selectors registered");
    }

    public void startSelectors() {
        LOGGER.info("Start selectors");

        for (int i = 0; i < 60; i++) {
            selectorList.get(i).resume();
            ;
        }

        LOGGER.info("Selectors started");
    }

    public void pauseSelectors() {
        LOGGER.info("Pause selectors");

        for (int i = 0; i < 60; i++) {
            selectorList.get(i).pause();
            ;
        }

        LOGGER.info("Selectors paused");
    }

    public void stopSelectors() {
        LOGGER.info("Stop selectors");

        for (int i = 0; i < 60; i++) {
            selectorList.get(i).stop();
            ;
        }

        LOGGER.info("Selectors stopped");
    }

}
