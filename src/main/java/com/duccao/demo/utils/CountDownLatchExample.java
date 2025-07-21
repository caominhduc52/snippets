package com.duccao.demo.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class demonstrates the usage of CountDownLatch in Java.
 * 
 * <h2>Overview</h2>
 * 
 * CountDownLatch is a synchronization aid that allows one or more threads to wait until
 * a set of operations being performed in other threads completes. A CountDownLatch is
 * initialized with a given count. The await methods block until the current count reaches
 * zero due to invocations of the countDown() method, after which all waiting threads are
 * released and any subsequent invocations of await return immediately.
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li><b>One-time use:</b> Once the count reaches zero, it cannot be reset. If you need a reusable version, consider using CyclicBarrier.</li>
 *   <li><b>Flexible waiting:</b> Threads can call await() at any time. If the count is already zero, they will proceed immediately.</li>
 *   <li><b>Timeout capability:</b> The await(long timeout, TimeUnit unit) method allows threads to wait for a specified maximum time.</li>
 *   <li><b>Interruptible:</b> The waiting threads can be interrupted.</li>
 * </ul>
 * 
 * <h2>Common Use Cases</h2>
 * 
 * <h3>1. Waiting for Multiple Tasks to Complete</h3>
 * <pre>
 * CountDownLatch latch = new CountDownLatch(N); // N is the number of tasks
 * 
 * // In each worker thread:
 * try {
 *     // Do work
 * } finally {
 *     latch.countDown();
 * }
 * 
 * // In the waiting thread:
 * latch.await(); // Wait for all N tasks to complete
 * </pre>
 * 
 * <h3>2. Starting Multiple Threads Simultaneously</h3>
 * <pre>
 * CountDownLatch startSignal = new CountDownLatch(1);
 * 
 * // In each worker thread:
 * startSignal.await(); // Wait for the signal
 * // Start work
 * 
 * // In the controlling thread:
 * // When ready to start all threads:
 * startSignal.countDown();
 * </pre>
 * 
 * <h3>3. Waiting for Resources to Initialize</h3>
 * <pre>
 * CountDownLatch resourcesReady = new CountDownLatch(requiredResourceCount);
 * 
 * // In resource initialization threads:
 * // Initialize resource
 * resourcesReady.countDown();
 * 
 * // In the thread that needs the resources:
 * resourcesReady.await(); // Wait until all resources are ready
 * </pre>
 * 
 * <h2>When to Use CountDownLatch vs Other Concurrency Utilities</h2>
 * <ul>
 *   <li>Use CountDownLatch when you need a one-time synchronization point</li>
 *   <li>Use CyclicBarrier when you need a reusable synchronization point</li>
 *   <li>Use Semaphore when you need to control access to a limited resource</li>
 *   <li>Use Phaser when you need a more flexible synchronization barrier with dynamic registration</li>
 * </ul>
 */
public class CountDownLatchExample {

    /**
     * Demonstrates how to use CountDownLatch to wait for multiple tasks to complete.
     * 
     * @param taskCount The number of tasks to execute
     * @param timeoutSeconds Maximum time to wait for all tasks to complete (in seconds)
     * @return true if all tasks completed successfully, false if timeout occurred
     */
    public static boolean executeParallelTasks(int taskCount, long timeoutSeconds) {
        // Create a CountDownLatch with a count equal to the number of tasks
        CountDownLatch latch = new CountDownLatch(taskCount);

        // Create a thread pool with a fixed number of threads
        ExecutorService executor = Executors.newFixedThreadPool(taskCount);

        try {
            // Submit tasks to the executor
            for (int i = 0; i < taskCount; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    try {
                        // Simulate work being done
                        System.out.println("Task " + taskId + " started");
                        Thread.sleep((long) (Math.random() * 1000));
                        System.out.println("Task " + taskId + " completed");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Task " + taskId + " was interrupted");
                    } finally {
                        // Count down the latch when the task completes
                        latch.countDown();
                        System.out.println("Latch count: " + latch.getCount());
                    }
                });
            }

            // Wait for all tasks to complete or timeout
            boolean completed = latch.await(timeoutSeconds, TimeUnit.SECONDS);

            if (completed) {
                System.out.println("All tasks completed successfully");
            } else {
                System.out.println("Timeout occurred before all tasks could complete");
            }

            return completed;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main thread was interrupted while waiting for tasks to complete");
            return false;
        } finally {
            // Shutdown the executor
            executor.shutdownNow();
        }
    }

    /**
     * Demonstrates how to use CountDownLatch as a starting signal for multiple threads.
     * All threads will wait until the latch is counted down, then start simultaneously.
     * 
     * @param threadCount The number of threads to start
     */
    public static void startThreadsSimultaneously(int threadCount) {
        // Create a CountDownLatch with count 1 (used as a starting gate)
        CountDownLatch startSignal = new CountDownLatch(1);

        // Create a CountDownLatch to wait for all threads to finish
        CountDownLatch doneSignal = new CountDownLatch(threadCount);

        // Create and start threads
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    System.out.println("Thread " + threadId + " ready and waiting for start signal");

                    // Wait for the start signal
                    startSignal.await();

                    // All threads will start at approximately the same time
                    System.out.println("Thread " + threadId + " started at: " + System.currentTimeMillis());

                    // Simulate work
                    Thread.sleep((long) (Math.random() * 1000));

                    System.out.println("Thread " + threadId + " finished");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // Signal that this thread is done
                    doneSignal.countDown();
                }
            }).start();
        }

        try {
            // Give threads time to initialize and wait on the start signal
            Thread.sleep(1000);

            System.out.println("All threads ready, releasing start signal");

            // Release all waiting threads
            startSignal.countDown();

            // Wait for all threads to finish
            doneSignal.await();

            System.out.println("All threads have finished execution");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main thread was interrupted");
        }
    }
}
