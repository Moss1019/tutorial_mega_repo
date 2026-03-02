package com.mosson.messaging;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;

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

    public PulsarConsumer(String pulsarUrl,
                          String tenant,
                          String namespace,
                          String topicName,
                          boolean isPersistent,
                          java.util.function.Consumer<byte[]> messageHandler) {
        this.messageHandler = messageHandler;
        try {
            client = PulsarClient.builder()
                    .serviceUrl(pulsarUrl)
                    .build();
            var topicPath = String.format("%s://%s/%s/%s",
                    (isPersistent ? "persistent" : "non-persistent"),
                    tenant,
                    namespace,
                    topicName);
            consumer = client.newConsumer()
                    .topic(topicPath)
                    .subscriptionName("PulsarTutorial")
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                    .consumerName("PulsarTutorial")
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
            var buffer = message.getData();
            consumer.acknowledge(message);
            return buffer;
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
            return null;
        }
    }

    private void doWork() {
        if(messageHandler == null) {
            inError = true;
            error = "Message handler not set";
            return;
        }
        while(isRunning) {
            var buffer = receive();
            messageHandler.accept(buffer);
        }
    }

    @Override
    public void close() {
        stop();
        try {
            consumer.close();
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
        }
        try {
            client.close();
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
        }
        worker.shutdown();
        worker.close();
    }
}
