package com.mosson.amqproducer;

import com.mosson.amqproducer.messaging.QueueProducer;

public class Program {
    public static void main(String[] args) {
        var isRunning = true;
        try (var queueProducer = new QueueProducer("amqp://localhost:5672", "messages-in")) {
            while(isRunning) {
                var input = getInput();
                if(input.equals("-q")) {
                    isRunning = false;
                } else {
                    queueProducer.send(input);
                }
            }
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
