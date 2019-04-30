package ru.hse.threadpool;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolImplTest {
    private ThreadPool threadPool;

    /**
     * This is tasks to run in the pool. Calculation of times and correct answer in the main.
     */
    private static Supplier<Integer> shortTask = () -> {
        int result = 0;
        int n = 10;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result = result ^ (result + i) + j * result;
            }
        }

        return result;
    };

    private static int shortTaskResult = 20936074;
    //such fast very speed

    private static Supplier<Integer> longerTask = () -> {
        int result = 0;
        int n = 10000;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result = result ^ (result + i) + j * result;
            }
        }

        return result;
    };

    private static int longerTaskResult = 1820229488;
    //~~0.15 second

    public static void main(String[] argc) {

        long startTime = System.currentTimeMillis();

        shortTaskResult = shortTask.get();

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println(elapsedTime);

        startTime = System.currentTimeMillis();

        longerTaskResult = longerTask.get();

        elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println(elapsedTime);

        System.out.println(shortTaskResult);
        System.out.println(longerTaskResult);
    }

    private static final int SIZE = 1000;
    private Supplier<Integer>[] tasks;
    private LightFuture<Integer>[] results;

    @BeforeEach
    void initialiseTask() {
        threadPool = new ThreadPoolImpl(4);

        //noinspection unchecked
        tasks = (Supplier<Integer>[]) Array.newInstance(shortTask.getClass(), SIZE);
        //noinspection unchecked
        results = (LightFuture<Integer>[]) Array.newInstance(LightFuture.class, SIZE);
    }

    @AfterEach
    void shutUp() {
        threadPool.shutdown();
    }

    @Test
    void canSubmitTasksWithDifferentResultType() throws LightFuture.LightExecutionException {
        //Yeah, I'm kinda stupid, so I wasn't sure it does work.

        var task1 = threadPool.submit(() -> 5d);
        var task2 = threadPool.submit(() -> "5");
        var task3 = task2.thenApply(s -> Integer.parseInt((String) s));
        assertEquals(5d, task1.get());
        assertEquals("5", task2.get());
        assertEquals(5, task3.get());
    }

    @Test
    void shortAndLongerTasksReturnsCorrectResults() throws LightFuture.LightExecutionException {
        var task1 = threadPool.submit(shortTask);
        var task2 = threadPool.submit(longerTask);
        assertEquals(shortTaskResult, task1.get());
        assertEquals(longerTaskResult, task2.get());
    }

    @Test
    void worksCorrectlyOnOneThreadPool() throws LightFuture.LightExecutionException {
        var threadPool = new ThreadPoolImpl(1);

        for (int i = 0; i < SIZE; i++) {
            tasks[i] = shortTask;
        }

        for (int i = 0; i < tasks.length; i++) {
            results[i] = threadPool.submit(tasks[i]);
        }
        for (int i = 0; i < tasks.length; i++) {
            assertEquals(shortTaskResult, results[i].get());
        }
    }

    @Test
    void worksCorrectlyOnManyThreadPool() throws LightFuture.LightExecutionException {
        for (int i = 0; i < SIZE; i++) {
            tasks[i] = shortTask;
        }

        for (int i = 0; i < tasks.length; i++) {
            results[i] = threadPool.submit(tasks[i]);
        }
        for (int i = 0; i < tasks.length; i++) {
            assertEquals(shortTaskResult, results[i].get());
        }
    }

    @Test
    void getOnTaskWithExceptionThrowsLightFutureExceptionWithCorrectCause() {
        var task = threadPool.submit(() -> {
            throw new NullPointerException();
        });

        assertThrows(LightFuture.LightExecutionException.class, task::get);
        try {
            task.get();
        } catch (LightFuture.LightExecutionException e) {
            assertEquals(NullPointerException.class, e.getCause().getClass());
        }
    }

    @Test
    void getOnTaskAppliedAfterTaskWithExceptionThrowsLightFutureExceptionWithCorrectCause() {
        var task = threadPool.submit(() -> {
            throw new NullPointerException();
        });

        assertThrows(LightFuture.LightExecutionException.class, task::get);
        var task2 = task.thenApply((v) -> {
            throw new ArrayIndexOutOfBoundsException();
        });

        var task3 = task2.thenApply((v) -> {
            throw new ArrayIndexOutOfBoundsException();
        });

        assertThrows(LightFuture.LightExecutionException.class, task::get);
        assertThrows(LightFuture.LightExecutionException.class, task2::get);
        assertThrows(LightFuture.LightExecutionException.class, task3::get);

        try {
            task.get();
        } catch (LightFuture.LightExecutionException e) {
            assertEquals(NullPointerException.class, e.getCause().getClass());
        }

        try {
            task2.get();
        } catch (LightFuture.LightExecutionException e) {
            assertEquals(NullPointerException.class, e.getCause().getClass());
        }

        try {
            task3.get();
        } catch (LightFuture.LightExecutionException e) {
            assertEquals(NullPointerException.class, e.getCause().getClass());
        }
    }

    @Test
    void thenApplyCorrectlyAppliesApplier() throws LightFuture.LightExecutionException {
        var task1 = threadPool.submit(() -> 5);
        var task2 = task1.thenApply(a -> a + 5);
        assertEquals(10, task2.get());
    }

    @Test
    void shutdownFinishesAllTasksAndClosesThreads() throws LightFuture.LightExecutionException {
        for (int i = 0; i < 10; i++) {
            results[i] = threadPool.submit(longerTask);
        }

        threadPool.shutdown();

        for (int i = 0; i < 10; i++) {
            assertEquals(longerTaskResult, results[i].get());
        }
    }

    @Test
    void threadpoolThrowsExceptionOnSubmitAfterShutdown() throws LightFuture.LightExecutionException {
        threadPool.shutdown();

        assertThrows(RejectedExecutionException.class, () -> threadPool.submit(shortTask));
    }

    private static final int SIZE_IN_THIS_TEST = 100;
    private volatile boolean[][] wasNotEvaluated = new boolean[SIZE_IN_THIS_TEST][1];

    @Test
    void expressionsDoesNotEvaluatedTwice() throws LightFuture.LightExecutionException {
        for (int i = 0; i < SIZE_IN_THIS_TEST; i++) {
            final int i1 = i;
            wasNotEvaluated[i][0] = true;

            results[i] = threadPool.submit(() -> {
                if (wasNotEvaluated[i1][0]) {
                    wasNotEvaluated[i1][0] = false;
                    return 322;
                } else {
                    throw new RuntimeException("You cannot evaluate twice.");
                }
            });
        }

        for (int i = 0; i < SIZE_IN_THIS_TEST; i++) {
            final int i1 = i;
            assertDoesNotThrow(() -> results[i1].get());
        }
    }

    @Test
    void severalThenAppliesOnOneObjectWorksCorrectly() throws LightFuture.LightExecutionException {
        var task = threadPool.submit(longerTask);
        for (int i = 0; i < 100; i++) {
            results[i] = task.thenApply(a -> a + 1);
        }
    }
}