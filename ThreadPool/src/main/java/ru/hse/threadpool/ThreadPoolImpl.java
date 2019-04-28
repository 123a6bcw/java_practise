package ru.hse.threadpool;

import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl {
    public LightFuture<?> submit (Supplier<?> supplier) {
        var task = new LightFutureImpl<>(supplier);
        return task;
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
        private LightFuture toDoAfter = null;

        private LightFutureImpl(Supplier<ResultType> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean isReady() {
            return result != null;
        }

        @Override
        public ResultType get() {
            if (supplier == null) {
                return result;
            }

            synchronized (this) {
                if (supplier != null) {
                    result = supplier.get();
                    supplier = null;
                }
            }

            return result;
        }

        @Override
        public <TransformType> LightFuture<TransformType> thenApply(Function<? super ResultType, TransformType> applier) {
            //So there will be no unchecked cast warning. Optimizer will remove this anyway, I hope...
            var toDoAfter = new LightFutureImpl<TransformType>(() -> applier.apply(get()));
            this.toDoAfter = toDoAfter;
            return toDoAfter;
        }

        /**
         *
         */
        LightFuture getToDoAfter() {
            return toDoAfter;
        }
    }

}
