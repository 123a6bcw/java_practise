package ru.hse.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 */
public class ThreadPoolImpl implements ThreadPool {
    /**
     *
     */
    private Thread[] threads;

    /**
     *
     */
    private final MyThreadQueue tasks = new MyThreadQueue();

    /**
     *
     */
    public ThreadPoolImpl(int n) {
        threads = new Thread[n];

        for (int i = 0; i < n; i++) {
            threads[i] = new Thread(() -> {
                while (!Thread.interrupted()) {
                    var lightFutureImpl = tasks.remove();

                    if (Thread.interrupted()) {
                        break;
                    }

                    lightFutureImpl.calculate();

                    //Here I synchronize over an object that over threads has access to, so it's okay.
                    //noinspection SynchronizationOnLocalVariableOrMethodParameter
                    synchronized (lightFutureImpl) {
                        List<LightFutureImpl<?>> toDoAfterList = lightFutureImpl.getToDoAfterList();
                        if (toDoAfterList != null) {
                            for (var toDoAfter : toDoAfterList) {
                                submit(toDoAfter);
                            }
                        }
                    }
                }
            });

            threads[i].setDaemon(true);
        }

        for (var thread : threads) {
            thread.start();
        }
    }

    /**
     *
     */
    @Override
    public LightFuture submit(Supplier<?> supplier) {
        var task = new LightFutureImpl<>(supplier);
        tasks.add(task);
        return task;
    }

    /**
     *
     */
    private void submit(LightFutureImpl<?> task) {
        tasks.add(task);
    }

    /**
     *
     */
    @Override
    public void shutdown() {
        for (var thread : threads) {
            thread.interrupt();
        }
    }

    /**
     *
     */
    private static class MyThreadQueue {
        /**
         *
         */
        private Node empty = new Node(null);

        /**
         *
         */
        @NotNull
        private Node tail = empty;

        /**
         *
         */
        @NotNull
        private Node head = empty;

        /**
         *
         */
        private final Object addLock = new Object();

        /**
         *
         */
        private final Object removeLock = new Object();

        //TODO всякие NotNull

        /**
         *
         */
        private void add(LightFutureImpl<?> lightFutureImpl) {
            synchronized (addLock) {
                var newNode = new Node(lightFutureImpl);
                tail.prev = newNode;
                tail = newNode;

                removeLock.notify();
            }
        }

        /**
         *
         */
        private boolean isEmpty() {
            return head == empty;
        }

        /**
         *
         */
        private LightFutureImpl<?> remove() {
            synchronized (removeLock) {
                while (isEmpty()) {
                    try {
                        removeLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                var lightFutureImpl = head.lightFutureImpl;
                head = head.prev;
                return lightFutureImpl;
            }
        }

        /**
         *
         */
        private static class Node {
            private LightFutureImpl<?> lightFutureImpl;

            private Node prev;

            private Node(LightFutureImpl<?> lightFuture) {
                this.lightFutureImpl = lightFuture;
            }
        }
    }

    /**
     *
     */
    private class LightFutureImpl<ResultType> implements LightFuture<ResultType> {
        /**
         *
         */
        private ResultType result;

        /**
         *
         */
        private volatile Supplier<ResultType> supplier;

        /**
         *
         */
        private final List<LightFutureImpl<?>> toDoAfterList = new ArrayList<>();

        /**
         *
         */
        private LightFutureImpl(Supplier<ResultType> supplier) {
            this.supplier = supplier;
        }

        /**
         *
         */
        @Override
        public boolean isReady() {
            return result != null;
        }

        /**
         *
         */
        @Override
        public ResultType get() {
            while (!isReady()) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); //google
                }
            }

            return result;
        }

        /**
         *
         */
        private void calculate() {
            result = supplier.get();
            supplier = null;
            this.notifyAll();
        }

        /**
         *
         */
        @Override
        public LightFuture<?> thenApply(Function<? super ResultType, ?> applier) {
            var toDoAfter = new LightFutureImpl<>(() -> applier.apply(get()));

            synchronized (this) {
                if (!isReady()) {
                    toDoAfterList.add(toDoAfter);
                } else {
                    submit(toDoAfter);
                }
            }

            return toDoAfter;
        }

        /**
         *
         */
        private List<LightFutureImpl<?>> getToDoAfterList() {
            return toDoAfterList;
        }
    }
}