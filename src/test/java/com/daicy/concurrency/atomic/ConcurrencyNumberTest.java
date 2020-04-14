package com.daicy.concurrency.atomic;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.*;
import java.util.function.IntConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ConcurrencyNumberTest {

    // 请求总数
    public static int clientTotal = 5000;

    // 同时并发执行的线程数
    public static int threadTotal = 200;

    private Integer count = 0;

    private volatile Integer volatileInt = 0;

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Test
    public void atomicInteger_whenSizeWithoutConcurrentUpdates_thenCorrect() throws InterruptedException {
        concurrentUpdate(1);
        assertEquals(clientTotal, atomicInteger.get());
    }

    @Test
    public void theInt_whenSizeWithoutConcurrentUpdates_thenError() throws InterruptedException {
        concurrentUpdate(2);
        assertNotEquals(clientTotal, count.intValue());
    }

    @Test
    public void volatileInt_whenSizeWithoutConcurrentUpdates_thenError() throws InterruptedException {
        concurrentUpdate(3);
        assertNotEquals(clientTotal, volatileInt.intValue());
    }


    private void concurrentUpdate(int type) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(threadTotal);
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
        for (int i = 0; i < clientTotal; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    add(type);
                    semaphore.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
    }

    private void add(int type) {
        if (type == 1) {
            atomicInteger.incrementAndGet();
        } else if (type == 2) {
                count++;
        } else {
               volatileInt++;
        }
    }

    class Foo {
        private Semaphore stage1 = new Semaphore(1);
        private Semaphore stage2 = new Semaphore(0);
        private Semaphore stage3 = new Semaphore(0);

        public Foo() {

        }
        public void first(Runnable printFirst) throws InterruptedException {
            stage1.acquire();
            printFirst.run();
            stage2.release();
        }

        public void second(Runnable printSecond) throws InterruptedException {
            stage2.acquire();
            printSecond.run();
            stage3.release();
        }

        public void third(Runnable printThird) throws InterruptedException {
            stage3.acquire();
            printThird.run();
            stage1.release();
        }
    }

    class FooBar {
        private int n;

        private Semaphore stage1 = new Semaphore(1);
        private Semaphore stage2 = new Semaphore(0);

        public FooBar(int n) {
            this.n = n;
        }

        public void foo(Runnable printFoo) throws InterruptedException {
            for (int i = 0; i < n; i++) {
                stage1.acquire();
                printFoo.run();
                stage2.release();
            }
        }

        public void bar(Runnable printBar) throws InterruptedException {
            for (int i = 0; i < n; i++) {
                stage2.acquire();
                printBar.run();
                stage1.release();
            }
        }
    }

    class FizzBuzz {
        private int n;

        private AtomicInteger atomicInteger = new AtomicInteger(1);

        private Semaphore semaphore = new Semaphore(1);


        public FizzBuzz(int n) {
            this.n = n;
        }

        // printFizz.run() outputs "fizz".
        public void fizz(Runnable printFizz) throws InterruptedException {
            while (atomicInteger.get() <= n) {
                if (atomicInteger.get() % 3 == 0 && atomicInteger.get() % 5 != 0) {
                    semaphore.acquire();
                    printFizz.run();
                    atomicInteger.getAndIncrement();
                    semaphore.release();
                } else {
                    Thread.yield();
                }
            }
        }

        // printBuzz.run() outputs "buzz".
        public void buzz(Runnable printBuzz) throws InterruptedException {
            while (atomicInteger.get() <= n) {
                if (atomicInteger.get() % 5 == 0 && atomicInteger.get() % 3 != 0) {
                    semaphore.acquire();
                    printBuzz.run();
                    atomicInteger.getAndIncrement();
                    semaphore.release();
                } else {
                    Thread.yield();
                }
            }
        }

        // printFizzBuzz.run() outputs "fizzbuzz".
        public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
            while (atomicInteger.get() <= n) {
                if (atomicInteger.get() % 3 == 0 && atomicInteger.get() % 5 == 0) {
                    semaphore.acquire();
                    printFizzBuzz.run();
                    atomicInteger.getAndIncrement();
                    semaphore.release();
                } else {
                    Thread.yield();
                }
            }

        }

        // printNumber.accept(x) outputs "x", where x is an integer.
        public void number(IntConsumer printNumber) throws InterruptedException {
            while (atomicInteger.get() <= n) {
                if (atomicInteger.get() % 3 == 0 || atomicInteger.get() % 5 == 0) {
                    Thread.yield();
                } else {
                    semaphore.acquire();
                    printNumber.accept(atomicInteger.getAndIncrement());
                    semaphore.release();
                }
            }
        }
    }
}
