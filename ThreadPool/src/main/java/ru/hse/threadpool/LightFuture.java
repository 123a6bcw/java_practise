package ru.hse.threadpool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Simple interface for evaluating expressions with (probably) long execution time.
 */
public interface LightFuture<ResultType> {
    /**
     * Returns true if expression was already evaluated.
     */
    public boolean isReady();

    /**
     * Returns evaluated value. Blocks current thread until the expression is evaluated.
     *
     * If expressions throws exception during execution, this throws LightExecutionException with original exception
     * as cause.
     */
    @Nullable
    public ResultType get() throws LightExecutionException;

    /**
     * Creates new LightFuture expression to evaluate over the result of the original one.
     *
     * If original LightFuture object throws an exception 'e' during execution, calling get on new object throws
     * LightExecutionException with 'e' as cause.
     */
    @NotNull
    public <TransformType> LightFuture<TransformType> thenApply(@NotNull Function<? super ResultType, TransformType> applier);

    /**
     * Exception of evaluating LightFuture expressions. If was created as a result of exception during execution of
     * the expression, stores this expressions as cause.
     */
    public class LightExecutionException extends Exception {
        LightExecutionException(String message) {
            super(message);
        }

        LightExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}