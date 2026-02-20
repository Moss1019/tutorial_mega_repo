package com.mosson.consumer.messaging;

import org.apache.pulsar.client.api.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PulsarConsumer implements AutoCloseable {
    private final ExecutorService worker = Executors.newSingleThreadExecutor();

    private final java.util.function.Consumer<byte[]> messageHandler;

    private PulsarClient client;

    private Consumer<byte[]> consumer;

    private boolean isRunning;

    private boolean inError;

    private String error;

    public PulsarConsumer(String serviceUrl, String tenant, String namespace, String topic, boolean isPersistent,
                          String subscriptionName, java.util.function.Consumer<byte[]> messageHandler) {
        this.messageHandler = messageHandler;
        try {
            client = PulsarClient
                    .builder()
                    .serviceUrl(serviceUrl)
                    .build();
            var topicPath = String.format("%s://%s/%s/%s",
                    (isPersistent ? "persistent" : "non-persistent"),
                    tenant,
                    namespace,
                    topic);
            consumer = client
                    .newConsumer()
                    .topic(topicPath)
                    .subscriptionName(subscriptionName)
                    .subscriptionType(SubscriptionType.Shared)
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                    .subscribe();
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
        worker.submit(this::doWork);
    }

    public void stop() {
        isRunning = false;
    }

    public byte[] receive() {
        if(inError) {
            return null;
        }
        try {
            var message = consumer.receive();
            var payload = message.getData();
            consumer.acknowledge(message);
            return payload;
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
            return null;
        }
    }

    private void doWork() {
        while(isRunning) {
            var payload = receive();
            messageHandler.accept(payload);
        }
    }

    @Override
    public void close() {
        stop();
        worker.shutdown();
        try {
            consumer.close();
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
        }
        try {
            client.close();
        }  catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
        }
    }
}
