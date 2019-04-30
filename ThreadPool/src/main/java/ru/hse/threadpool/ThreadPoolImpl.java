package ru.hse.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
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
                    if ()
                }
            });

            threads[i].setDaemon(true);
        }
    }

    /**
     *
     */
    public LightFuture submit(Supplier<?> supplier) {
        var task = new LightFutureImpl<>(supplier);
        tasks.add(task);
        return task;
    }

    /**
     *
     */
    private void submit(LightFutureImpl task) {
        tasks.add(task);
    }

    /**
     *
     */
    public void shutdown() {
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

        /**
         *
         */
        private void add(LightFutureImpl lightFutureImpl) {
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
        private LightFutureImpl remove() {
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
            private LightFutureImpl lightFutureImpl;

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
        private final List<LightFuture> toDoAfterList = new ArrayList<>();

        /**
         *
         */
        private LightFutureImpl(Supplier<ResultType> supplier) {
            this.supplier = supplier;
        }

        /**
         *
         */
        private boolean notCalculated() {
            return supplier != null;
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
            while (notCalculated()) {
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
        public <TransformType> LightFuture<TransformType> thenApply(Function<? super ResultType, TransformType> applier) {
            var toDoAfter = new LightFutureImpl<TransformType>(() -> applier.apply(get());

            synchronized (this) {
                if (notCalculated()) {
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
        List<LightFuture> getToDoAfterList() {
            return toDoAfterList;
        }
    }
}