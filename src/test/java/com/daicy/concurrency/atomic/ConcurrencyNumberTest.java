package com.daicy.concurrency.atomic;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
        private AtomicInteger atomicInteger = new AtomicInteger(1);

        public Foo() {

        }
        public void first(Runnable printFirst) throws InterruptedException {
            while (atomicInteger.get() % 1 != 0){
                // printFirst.run() outputs "first". Do not change or remove this line.
            }
            printFirst.run();
            atomicInteger.incrementAndGet();
        }

        public void second(Runnable printSecond) throws InterruptedException {
            while (atomicInteger.get() % 2 != 0){
                // printFirst.run() outputs "first". Do not change or remove this line.
            }
            printSecond.run();
            atomicInteger.incrementAndGet();
            // printSecond.run() outputs "second". Do not change or remove this line.
        }

        public void third(Runnable printThird) throws InterruptedException {
            while (atomicInteger.get() % 3 != 0) {
            }
                // printThird.run() outputs "third". Do not change or remove this line.
            printThird.run();
            atomicInteger.incrementAndGet();
        }
    }

    class FooBar {
        private int n;

        private AtomicReference<Integer> atomicInteger = new AtomicReference<>();

        public FooBar(int n) {
            this.n =  n;
            this.atomicInteger.set(0);
        }

        public void foo(Runnable printFoo) throws InterruptedException {
            while (atomicInteger.get() < n) {
                if(atomicInteger.get() % 1 == 0){
                    printFoo.run();
                    atomicInteger.compareAndSet(atomicInteger.get(),atomicInteger.get()+1);
                }
            }
        }

        public void bar(Runnable printBar) throws InterruptedException {
            while (atomicInteger.get() < n) {
                if(atomicInteger.get() % 2 == 0){
                    printBar.run();
                    atomicInteger.compareAndSet(atomicInteger.get(),atomicInteger.get()+1);
                }
            }
        }
    }
}
