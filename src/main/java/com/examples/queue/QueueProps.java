package com.examples.queue;

public interface QueueProps {

	String QUEUE_CON_FACTORY = "java:/ConnectionFactory";
	String SELECTOR_QUEUE = "java:/jms/queue/SelectorQueue";
	String MDB_QUEUE = "java:/jms/queue/TestMDBQueue";
	String SELECTOR_PREFIX = "SELECTOR";
	String SELECTOR_ID_KEY = "SELECTOR_ID";
}
