package com.mosson.amqconsumer;

import com.mosson.amqconsumer.messaging.QueueConsumer;

public class Program {
    public static void main(String[] args) {
        var isRunning = true;
        try(var queueConsumer = new QueueConsumer("amqp://localhost:5672", "messages-in", System.out::println)) {
            queueConsumer.start();
            while(isRunning) {
                var input = getInput();
                if(input.equals("-q")) {
                    isRunning = false;
                }
            }
            queueConsumer.stop();
        }
    }

    private static String getInput() {
        var buffer = new byte[1024];
        try {
            var read = System.in.read(buffer);
            return new String(buffer, 0, read - 1);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "";
        }
    }
}
