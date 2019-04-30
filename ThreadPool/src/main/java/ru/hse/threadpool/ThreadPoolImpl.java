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

                    //Here I synchronize over an object that over threads has access to, so it's okay.
                    //noinspection SynchronizationOnLocalVariableOrMethodParameter
                    synchronized (lightFutureImpl) {
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
        if (Thread.currentThread().isInterrupted()) {
            throw new RejectedExecutionException("The pool was shut down, no new task can be submitted");
        }

        var task = new LightFutureImpl<>(supplier);
        tasks.add(task);
        return task;
    }

    /**
     * Submit previously created LightFutureImpl to the pool. Being used when task being creates upon a result of another
     * LightFutureImpl, but this task has been already finished and left the pool.
     */
    private <R> void submit(@NotNull LightFutureImpl<R> task) {
        if (Thread.currentThread().isInterrupted()) {
            throw new RejectedExecutionException("The pool was shut down, no new task can be submitted");
        }

        tasks.add(task);
    }

    @Override
    public void shutdown() {
        for (var thread : threads) {
            thread.interrupt();
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
         * Does not force expression to evaluate, only force current method to wait until it's done.
         */
        @Override
        public ResultType get() throws LightExecutionException {
            if (!isReady()) {
                synchronized (this) {
                    while (!isReady()) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
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
            synchronized (this) {
                //Supplier is null iff someone called this method before. We make sure that does not happen in ThreadPoolImpl.
                try {
                    result = Objects.requireNonNull(supplier).get();
                } catch (Throwable e) {
                    /*
                    I am dirty hacker, in order to throw exception from original task to thenApplied task I store it in
                    unchecked exception.

                    All because I cannot throw exceptions in a freaking Supplier<R>.
                     */
                    var suppressed = e.getSuppressed();
                    if (e instanceof RuntimeException && suppressed.length == 1 && suppressed[0] instanceof lolKekCheburek) {
                        e = ((lolKekCheburek) suppressed[0]).getException().getCause();
                    }

                    exceptionOnExecution = new LightExecutionException("Exception during execution of the given supplier", e);
                }

                this.supplier = null;

                this.notifyAll();
            }
        }

        /**
         * If original task was already finished, this submit new task directly to the thread pool.
         */
        @NotNull
        @Override
        public LightFuture<?> thenApply(@NotNull Function<? super ResultType, ?> applier) {
            var toDoAfter = new LightFutureImpl<>(() -> {
                try {
                    return applier.apply(get());
                } catch (LightExecutionException e) {
                    /*
                    I am dirty hacker, I know, but I never came up with a better solution...
                     */
                    var toThrow = new RuntimeException();
                    toThrow.addSuppressed(new lolKekCheburek(e));
                    throw toThrow;
                }
            });

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
         * Returns list of task that should evaluate on the result of the given one.
         */
        @NotNull
        private List<LightFutureImpl<?>> getToDoAfterList() {
            return toDoAfterList;
        }
    }

    /**
     * Secret wrapper for exception, so user (not hacker) could not get this exception.
     */
    private class lolKekCheburek extends Exception {
        private final Exception exception;

        private lolKekCheburek(Exception exception) {
            this.exception = exception;
        }

        private Exception getException() {
            return exception;
        }
    }
}