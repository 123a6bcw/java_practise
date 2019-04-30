package ru.hse.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Simple thread pool executor.
 */
public interface ThreadPool {
    /**
     * Submit task to the pool. Returns LightFuture object which represent a value that will be evaluated in a future.
     *
     * Blablabla exceptions
     */
    @NotNull
    public LightFuture<?> submit(@NotNull Supplier<?> supplier);

    /**
     * Shuts the pool, all unfinished tasks will be finished, but new submitted task will cause RejectedExecutionException.
     */
    public void shutdown();
}
