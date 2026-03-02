package com.mosson.messaging;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;

public class PulsarProducer implements AutoCloseable {
    private PulsarClient client;

    private Producer<byte[]> producer;

    private boolean inError;

    private String error;

    public PulsarProducer(String pulsarUrl,
                          String tenant,
                          String namespace,
                          String topicName,
                          boolean isPersistent) {
        try {
            client = PulsarClient.builder()
                    .serviceUrl(pulsarUrl)
                    .build();
            var topicPath = String.format("%s://%s/%s/%s",
                    (isPersistent ? "persistent" : "non-persistent"),
                    tenant,
                    namespace,
                    topicName);
            producer = client.newProducer()
                    .topic(topicPath)
                    .producerName("PulsarTutorial")
                    .create();
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

    public boolean send(byte[] buffer) {
        if(inError) {
            return false;
        }
        try {
            producer.send(buffer);
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
            client.close();
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
        }
    }
}
