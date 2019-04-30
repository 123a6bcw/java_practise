package ru.hse.threadpool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolImplTest {
    @Test
    void justTest() {
        var threadPool = new ThreadPoolImpl(1);
        var ftr = threadPool.submit(() -> 5);
        try {
            System.out.println(ftr.get());
        } catch (LightFuture.LightExecutionException e) {
            e.printStackTrace();
        }
    }
}