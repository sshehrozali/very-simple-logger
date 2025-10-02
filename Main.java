package org.strategypattern;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

        class Logger {
            public Logger() {
                var writer = new Thread(() -> {
                    var n = 0;
                    while (true) {
                        if (queue.peek() != null) {
                            System.out.printf(Thread.currentThread().getName() + ": %s\n", queue.poll());
                        }
                    }
                });

                writer.setDaemon(false); // until JVM is alive
                writer.start();
            }

            void write(String in) {
                queue.offer(in);
            }
        }


        var logger = new Logger();

        /*
        * Example two threads concurrently performing I/O (API call, File system)
        * */
        var serviceA = new Thread(() -> {
            var n = 0;
            while (n < 100) {
                logger.write("[Service A]: %s".formatted(n));
                n++;
                try {
                    Thread.sleep(1000); // demonstrating high latency
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        var serviceB = new Thread(() -> {
            var n = 0;
            while (n < 100) {
                logger.write("[Service B]: %s".formatted(n));
                n++;
                try {
                    Thread.sleep(100); // demonstrating low latency
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        serviceA.join();
        serviceB.join();
        serviceA.start();
        serviceB.start();
    }
}
