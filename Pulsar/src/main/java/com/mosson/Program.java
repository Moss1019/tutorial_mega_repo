package com.mosson;

import com.mosson.messaging.PulsarConsumer;
import com.mosson.messaging.PulsarProducer;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Program {
    static void main() {
        System.out.println(UUID.randomUUID().toString().length());
//        try(var producer = new PulsarProducer("pulsar://localhost:6650",
//                "test-tenant",
//                "test-ns",
//                "test-topic-1",
//                true);
//            var consumer = new PulsarConsumer("pulsar://localhost:6650",
//                    "test-tenant",
//                    "test-ns",
//                    "test-topic-1",
//                    true,
//                    Program::handleMessage)) {
//
//            consumer.start();
//
//            var isRunning = true;
//            while(isRunning) {
//                var input = getInput();
//                if(input.equals("-q")) {
//                    isRunning = false;
//                } else {
//                    producer.send(input.getBytes(StandardCharsets.UTF_8));
//                }
//            }
//
//            consumer.stop();
//        }
    }

    private static String getInput() {
        try {
            var buffer = new byte[1024];
            var read = System.in.read(buffer);
            return new String(buffer, 0, read - 1);
//            return new String(buffer).trim();
        } catch (Exception ignored) {
            return "";
        }
    }

    private static void handleMessage(byte[] buffer) {
        System.out.println(new String(buffer));
    }
}
