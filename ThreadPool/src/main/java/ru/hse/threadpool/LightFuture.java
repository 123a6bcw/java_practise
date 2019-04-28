package ru.hse.threadpool;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Simple interface for evaluating expressions with (probably) long execution time.
 */
public interface LightFuture<ResultType> {
    /** /TODO
     * Returns true if expression was evaluated.
     */
    public boolean isReady();

    /** /TODO
     * Returns evaluated value.
     *
     * If expressions throws exception during execution, this throws LightExecutionException with original exception
     * as cause.
     */
    public ResultType get();

    /** /TODO
     * Creates new LightFuture expression to evaluate over the result of the original one.
     *
     * If original LightFuture object throws and exception during execution, calling get on new object throws
     * LightExecutionException with original LightExecutionException as cause.
     */
    public <TransformType> LightFuture<TransformType> thenApply(Function<? super ResultType, TransformType> applier);

    /** /TODO
     * Exception of evaluating LightFuture expressions. If was created as a result of exception during execution of
     * the expression, stores
     */
    public class LightExecutionException extends RuntimeException {
    }
}
