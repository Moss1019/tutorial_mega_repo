package com.mosson.producer;

import com.mosson.producer.messaging.PulsarProducer;
import org.apache.pulsar.client.api.*;

import java.nio.charset.StandardCharsets;

public class Program {
    public static void main(String[] args) {
        var producer = new PulsarProducer("pulsar://localhost:6650",
                "test-tenant",
                "ns1",
                "test-topic-new",
                true);

        var isRunning = true;
        while(isRunning) {
            var input = getInput();
            if(input.equals("-q")) {
                isRunning = false;
            } else {
                producer.send(input.getBytes(StandardCharsets.UTF_8));
            }
        }

        if(producer.isInError()) {
            System.out.println(producer.getError());
        }

        producer.close();
    }

    private static String getInput() {
        var buffer = new byte[1024];
        try {
            var read = System.in.read(buffer);
            return new String(buffer, 0, read - 1);
        } catch (Exception ignored) {
            return "";
        }
    }
}
