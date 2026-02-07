package com.mosson.amqproducer.messaging;

import jakarta.jms.Connection;
import jakarta.jms.Destination;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import org.apache.qpid.jms.JmsConnectionFactory;

import java.time.Duration;

public class QueueProducer implements AutoCloseable {
    private Connection connection;

    private Session session;

    private MessageProducer producer;

    private boolean inError;

    private String error;

    public QueueProducer(String brokerUrl, String queueName) {
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
            producer = session.createProducer(destination);
            producer.setTimeToLive(Duration.ofMinutes(2).toMillis());
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

    public boolean send(String content) {
        if(inError) {
            return false;
        }
        try {
            var message = session.createTextMessage(content);
            producer.send(message);
            return true;
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
            return false;
        }
    }

    @Override
    public void close() {
        try {
            producer.close();
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
