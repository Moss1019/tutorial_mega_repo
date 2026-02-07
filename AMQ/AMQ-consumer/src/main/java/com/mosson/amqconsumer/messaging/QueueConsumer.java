package com.mosson.amqconsumer.messaging;

import jakarta.jms.Connection;
import jakarta.jms.Destination;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;
import org.apache.qpid.jms.JmsConnectionFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class QueueConsumer implements AutoCloseable {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Consumer<String> messageHandler;

    private Connection connection;

    private Session session;

    private MessageConsumer consumer;

    private boolean isRunning;

    private boolean inError;

    private String error;

    public QueueConsumer(String brokerUrl, String queueName, Consumer<String> messageHandler) {
        this.messageHandler = messageHandler;
        var connectionFactory = new JmsConnectionFactory(brokerUrl);
        try {
            connection = connectionFactory.createConnection();
            connection.start();
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
            return;
        }
        try {
            session = connection.createSession(Session.AUTO_ACKNOWLEDGE);
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
            return;
        }
        Destination destination;
        try {
            destination = session.createQueue(queueName);
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
            return;
        }
        try {
            consumer = session.createConsumer(destination);
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
        }
    }

    public boolean isInError() {
        return inError;
    }

    public String getError() {
        return error;
    }

    public void start() {
        isRunning = true;
        executor.submit(this::doWork);
    }

    public void stop() {
        isRunning = false;
    }

    private void doWork() {
        while(isRunning) {
            if(!receive()) {
                isRunning = false;
            }
        }
    }

    public boolean receive() {
        if (inError) {
            return false;
        }
        try {
            var message = consumer.receive();
            if(message != null) {
                var content = message.getBody(String.class);
                messageHandler.accept(content);
            }
            return true;
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
            return false;
        }
    }

    @Override
    public void close() {
        stop();
        executor.shutdown();
        try {
            consumer.close();
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
        }
        try {
            session.close();
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
        }
        try {
            connection.stop();
            connection.close();
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
        }
    }

}
