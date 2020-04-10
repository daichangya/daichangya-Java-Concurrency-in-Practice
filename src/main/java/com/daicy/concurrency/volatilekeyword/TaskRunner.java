package com.daicy.concurrency.volatilekeyword;

public class TaskRunner {

    private static int number;
    private volatile static boolean ready;

    private static class Reader extends Thread {

        @Override
        public void run() {
            while (!ready) {
                System.out.println("run!");
                Thread.yield();
            }

            System.out.println(number);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Reader().start();
        number = 42;
        ready = true;
    }
}
