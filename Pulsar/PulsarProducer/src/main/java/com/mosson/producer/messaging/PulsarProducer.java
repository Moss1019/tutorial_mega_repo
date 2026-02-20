package com.mosson.producer.messaging;

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;

public class PulsarProducer implements AutoCloseable {
    private PulsarClient client;

    private Producer<byte[]> producer;

    private boolean inError;

    private String error;

    public PulsarProducer(String serviceUrl, String tenant, String namespace, String topic, boolean isPersistent) {
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
            producer = client
                    .newProducer()
                    .topic(topicPath)
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

    public MessageId send(byte[] buffer) {
        if(inError) {
            return null;
        }
        try {
            return producer.send(buffer);
        } catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
            return null;
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
        }  catch (Exception ex) {
            inError = true;
            error = ex.getMessage();
        }
    }
}
