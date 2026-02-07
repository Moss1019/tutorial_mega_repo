package com.mosson.amqproducer.messaging;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QueueProducerTest {
    @Test()
    public void testCreation() {
        try(var sut = new QueueProducer("amqp://localhost:5672", "queTest")) {
            Assertions.assertFalse(sut.isInError(), sut.getError());

            Assertions.assertTrue(sut.send("Hello artemis"), sut.getError());
        }

        try(var sut = new QueueProducer("amqp://localhost:6672", "queTest")) {
            Assertions.assertFalse(sut.isInError(), sut.getError());

            Assertions.assertTrue(sut.send("Hello activeMQ"), sut.getError());
        }
    }
}
