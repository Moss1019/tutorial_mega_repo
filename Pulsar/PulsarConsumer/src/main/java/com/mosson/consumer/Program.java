package com.mosson.consumer;

import com.mosson.consumer.messaging.PulsarConsumer;

public class Program {
    public static void main(String[] args) {
        var consumer = new PulsarConsumer("pulsar://localhost:6650", "test-tenant", "ns1", "test-topic-new", true,
                "test-subscription", (buffer) -> {
            System.out.println("Consumer 1");
            Program.processData(buffer);
        });
        consumer.start();
        var consumer2 = new PulsarConsumer("pulsar://localhost:6650", "test-tenant", "ns1", "test-topic-new", true,
                "test-subscription", (buffer) -> {
            System.out.println("Consumer 2");
            Program.processData(buffer);
        });
        consumer2.start();

        var isRunning = true;
        while(isRunning) {
            var input = getInput();
            if(input.equals("-q")) {
                isRunning = false;
            } else {
                if(consumer.isInError()) {
                    System.out.println(consumer.getError());
                } else {
                    System.out.println("No error");
                }
            }
        }

        consumer.close();
        consumer2.close();
    }

    private static void processData(byte[] buffer) {
        System.out.println(new String(buffer));
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
