package ru.hse.threadpool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Implementation of ThreadPool with fixed number of threads.
 */
public class ThreadPoolImpl implements ThreadPool {
    /**
     * Working poll's threads.
     */
    private Thread[] threads;

    /**
     * Queue of tasks in the pool.
     */
    private final MyThreadQueue tasks = new MyThreadQueue();

    /**
     * True if user has called shutdown();
     */
    private boolean isShuttedDown;

    /**
     * Object to syncronize over when checking case of threadpool being shutted down.
     */
    private final Object shutdownLock = new Object();

    /**
     * Number of task that are submitted to pool but not yet added to queue.
     */
    private int waitingToAddCount = 0;

    /**
     * Creates n threads and starts it.
     */
    @SuppressWarnings("WeakerAccess")
    public ThreadPoolImpl(int n) {
        threads = new Thread[n];

        for (int i = 0; i < n; i++) {
            threads[i] = new Thread(() -> {
                while (true) {
                    LightFutureImpl<?> lightFutureImpl;

                    try {
                        lightFutureImpl = tasks.remove();
                    } catch (InterruptedException e) {
                        break;
                    }

                    lightFutureImpl.evaluate();

                    synchronized (lightFutureImpl.getToDoAfterList()) {
                        List<LightFutureImpl<?>> toDoAfterList = lightFutureImpl.getToDoAfterList();
                        for (var toDoAfter : toDoAfterList) {
                            submit(toDoAfter);
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

    @Override
    @NotNull
    public <R> LightFuture<R> submit(@NotNull Supplier<R> supplier) {
        checkForShutdown();

        var task = new LightFutureImpl<>(supplier);
        tasks.add(task);

        decreaseWaitingToAddCount();

        return task;
    }


    /**
     * Submit previously created LightFutureImpl to the pool. Being used when task being creates upon a result of another
     * LightFutureImpl, but this task has been already finished and left the pool.
     */
    private <R> void submit(@NotNull LightFutureImpl<R> task) {
        checkForShutdown();

        tasks.add(task);

        decreaseWaitingToAddCount();
    }

    /**
     * Decrease number of tasks that are submitted to pool but not yet added to impl queue.
     * If all task have benn submitted, notifies thread with shutdown.
     */
    private void decreaseWaitingToAddCount() {
        synchronized (shutdownLock) {
            waitingToAddCount--;
            if (waitingToAddCount == 0) {
                shutdownLock.notify();
            }
        }
    }

    @Override
    public void shutdown() {
        synchronized (shutdownLock) {
            isShuttedDown = true;

            while (waitingToAddCount > 0) {
                try {
                    shutdownLock.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }

        for (var thread : threads) {
            thread.interrupt();
        }
    }

    /**
     * Throws exception if thread is shutted down (so we cannot submit task to it). Otherwise adds task to the number of task
     * that are sumbitted to pool but not yet added to queue.
     */
    private void checkForShutdown() {
        synchronized (shutdownLock) {
            if (isShuttedDown) {
                throw new RejectedExecutionException("The pool was shut down, no new task can be submitted");
            } else {
                waitingToAddCount++;
            }
        }
    }

    /**
     * Thread safe single connected queue with separate blocks on adding elements and removing. Stores only LightFutureImpl.
     */
    private static class MyThreadQueue {
        /**
         * Tail of the queue aka the last element.
         */
        @Nullable
        private Node tail = null;

        /**
         * Head of the queue aka the first element.
         */
        @Nullable
        private Node head = null;

        /**
         * In order to change tail (for example to add element to the queue),
         * threads synchronize over this object (they cannot sync on tail cause tail is changing)
         */
        @NotNull
        private final Object tailLock = new Object();

        /**
         * Same for head.
         */
        @NotNull
        private final Object headLock = new Object();

        /**
         * Thread-safe add to queue.
         */
        private void add(@NotNull LightFutureImpl<?> lightFutureImpl) {
            var newNode = new Node(lightFutureImpl);

            synchronized (tailLock) {
                //Now no one can change tail except for us, because we locked on it.
                if (tail == null) {
                    /*And if it is null, our queue is empty, therefore head == null too and we need to change it, so
                     we locks on headLock.

                     And there is no deadlock here, because we try to lock on headLock in a situation tail == head == null,
                     but in such circumstances remove() releases it's headLock and waits.
                     */
                    synchronized (headLock) {
                        tail = newNode;
                        head = newNode;
                        headLock.notify(); //We also notify a guy who was waiting for an element to appear in empty queue.
                        return;
                    }
                }

                tail.prev = newNode;
                tail = newNode;
            }
        }

        /**
         * Returns true is there is no elements in queue.
         */
        private boolean isEmpty() {
            return head == null;
        }

        /**
         * Thread-safe remove from queue. If there is no elements in queue, current thread waits until they appears.
         */
        @NotNull
        private LightFutureImpl<?> remove() throws InterruptedException {
            synchronized (headLock) {
                //Now only we can change head.
                while (isEmpty()) {
                    //But if there is no element in queue (head == null), we release our lock and wait until this elements appears.
                    headLock.wait();
                }

                var lightFutureImpl = head.lightFutureImpl;
                if (head == tail) {
                    /* We need to remove element, but this is the last element in queue, so we also have to change tail,
                     therefore we need to lock on tail.

                     No deadlock, because add() tries to lock on headLock only if tail is null, but in our situation
                     we are sure it is not, because there is at least one element in queue.
                     */
                    synchronized (tailLock) {
                        //But someone could add new elements in between, so we need double check.
                        if (head == tail) {
                            head = null;
                            tail = null;
                            return lightFutureImpl;
                        }
                    }
                }

                head = head.prev;
                headLock.notify(); //This was not a last element in queue, so we notify next guy who was waiting for elements.
                return lightFutureImpl;
            }
        }

        /**
         * Class that stores a singe LightFutureImpl object.
         */
        private static class Node {
            /**
             * Value.
             */
            @NotNull
            private LightFutureImpl<?> lightFutureImpl;

            /**
             * Previous Node in queue.
             */
            @Nullable
            private Node prev = null;

            private Node(@NotNull LightFutureImpl<?> lightFuture) {
                this.lightFutureImpl = lightFuture;
            }
        }
    }

    /**
     * Implementation of LightFuture connected to this implementation of threadpool.
     */
    //Not static, because thenApply may submit tasks to the pool.
    private class LightFutureImpl<ResultType> implements LightFuture<ResultType> {
        /**
         * Result value. May be null even after evaluating expression (if that is an actualy result of the expression).
         */
        @Nullable
        private ResultType result;

        /**
         * Supplier function for evaluating expression.
         * Null iff expression was evaluated.
         */
        @Nullable
        private volatile Supplier<? extends ResultType> supplier;

        /**
         * If evaluating expression causes and exception, it stores here.
         */
        @Nullable
        private LightExecutionException exceptionOnExecution = null;

        /**
         * List of expressions that should be evaluated on the result of this expression. Has sense only before
         * pool has evaluated this expressions (more accurately, before pool takes this list synchronized and add this
         * tasks to the pool).
         */
        @NotNull
        private final List<LightFutureImpl<?>> toDoAfterList = new ArrayList<>();

        private LightFutureImpl(@NotNull Supplier<? extends ResultType> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean isReady() {
            return supplier == null;
        }

        /**
         * Object to synchronize over to evaluate an expression supplied by this task.
         */
        private final Object evaluateLock = new Object();

        /**
         * Does not force expression to evaluate, only force current method to wait until it's done.
         */
        @Override
        public ResultType get() throws LightExecutionException {
            if (!isReady()) {
                synchronized (evaluateLock) {
                    while (!isReady()) {
                        try {
                            evaluateLock.wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }

            if (exceptionOnExecution != null) {
                throw exceptionOnExecution;
            }

             return result;
        }

        /**
         * Calculates value of the expression. Block current thread until it's done. After that, supplier is null.
         *
         * No real checks of using this method twice are done and no synchronization are being used because ThreadPoolImpl
         * realisation guarantees that exactly one thread will call this method.
         *
         * Throws exception if exectuion causes an exception.
         */
        private void evaluate() {
            synchronized (evaluateLock) {
                //Supplier is null iff someone called this method before. We make sure that does not happen in ThreadPoolImpl.
                try {
                    result = Objects.requireNonNull(supplier).get();
                } catch (Throwable e) {
                    if (parent != null && parent.exceptionOnExecution != null) {
                        e = parent.exceptionOnExecution.getCause();
                        exceptionOnExecution = parent.exceptionOnExecution;
                    }

                    exceptionOnExecution = new LightExecutionException("Exception during execution of the given supplier", e);
                }

                this.supplier = null;

                evaluateLock.notifyAll();
            }
        }

        /**
         * Not null if this task created via thenApply. Then it's link to the original task.
         */
        private LightFutureImpl<?> parent = null;

        private void setParent(LightFutureImpl<?> parent) {
            this.parent = parent;
        }

        /**
         * If original task was already finished, this submit new task directly to the thread pool.
         */
        @NotNull
        @Override
        public <TransformType> LightFuture<TransformType> thenApply(@NotNull Function<? super ResultType, TransformType> applier) {
            var toDoAfter = new LightFutureImpl<>(() -> {
                try {
                    return applier.apply(get());
                } catch (LightExecutionException e) {
                    throw new RuntimeException();
                }
            });

            toDoAfter.setParent(this);

            synchronized (toDoAfterList) {
                if (!isReady()) {
                    toDoAfterList.add(toDoAfter);
                } else {
                    submit(toDoAfter);
                }
            }

            return toDoAfter;
        }

        /**
         * Returns list of task that should evaluate on the result of the given one.
         */
        @NotNull
        private List<LightFutureImpl<?>> getToDoAfterList() {
            return toDoAfterList;
        }
    }
}