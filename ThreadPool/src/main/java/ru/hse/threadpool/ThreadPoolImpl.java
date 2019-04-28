package ru.hse.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 */
public class ThreadPoolImpl {
    /**
     *
     */
    private Thread[] threads;

    /**
     *
     */
    public ThreadPoolImpl(int n) {
        threads = new Thread[n];

        for (int i = 0; i < n; i++) {
            threads[i] = new Thread(() -> {
                while (true) {
                    if (Thread.interrupted()) {
                        break;
                    }

                    synchronized (tasks) {
                        if (tasks.isEmpty()) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                break;
                            }
                        }

                        var task = tasks.remove();

                        try {
                            task.get();
                        }
                    }
                }
            });

            threads[i].setDaemon(true);
        }
    }

    /**
     *
     */
    public LightFuture<?> submit (Supplier<?> supplier) {
        var task = new LightFutureImpl<>(supplier);



        return task;
    }

    /**
     *
     */
    private static class MyThreadList {
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

        /**
         *
         */
        private void add(LightFuture lightFuture) {
            synchronized (addLock) {
                var newNode = new Node(lightFuture);
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
        private LightFuture remove() {
            synchronized (removeLock) {
                while (isEmpty()) {
                    try {
                        removeLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); //google
                    }
                }

                var lightFuture = head.lightFuture;
                head = head.prev;
                return lightFuture;
            }
        }

        /**
         *
         */
        private static class Node {
            private LightFuture lightFuture;

            private Node prev;

            private Node(LightFuture<?> lightFuture) {
                this.lightFuture = lightFuture;
            }
        }
    }

    /**
     *
     */
    private static class LightFutureImpl<ResultType> implements LightFuture<ResultType> {
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
        private final List<LightFuture> toDoAfterList = new ArrayList<>();

        private LightFutureImpl(Supplier<ResultType> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean isReady() {
            return result != null;
        }

        @Override
        public ResultType get() {
            while (supplier != null) {
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

        @Override
        public <TransformType> LightFuture<TransformType> thenApply(Function<? super ResultType, TransformType> applier) {
            var toDoAfter = new LightFutureImpl<TransformType>(() -> applier.apply(get()));

            synchronized (toDoAfterList) {
                toDoAfterList.add(toDoAfter);
            }

            return toDoAfter;
        }

        /**
         *
         */
        List<LightFuture> getToDoAfterList() {
            return toDoAfterList;
        }
    }

}
