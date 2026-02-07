package com.mosson.amqconsumer.messaging;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QueueConsumerTest {
    @Test()
    public void testReceive() {
        try (var sut = new QueueConsumer("amqp://localhost:5672",
                "queTest",
                this::handleMessage)) {
           Assertions.assertTrue(sut.receive(), sut.getError());
        }

        try (var sut = new QueueConsumer("amqp://localhost:6672",
                "queTest",
                this::handleMessage)) {
            Assertions.assertTrue(sut.receive(), sut.getError());
        }
    }

    private void handleMessage(String message) {
        System.out.println(message);
    }
}
