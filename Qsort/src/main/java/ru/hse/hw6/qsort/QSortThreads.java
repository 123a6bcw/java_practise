package ru.hse.hw6.qsort;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Class for sorting arrays using quick sort algorithm and either using threads or not.
 */
public class QSortThreads {
    /**
     * If array's length is lower than this, then it's probably better to sort it without using threads as they only
     * slow down the process.
     */
    private static final int DEFAULT_MAX_LENGTH_FOR_THREADS = 1<<13;

    /**
     * Actual max length may differ from default if user wish to.
     */
    private static int ACTUAL_MAX_LENGTH_FOR_THREADS = DEFAULT_MAX_LENGTH_FOR_THREADS;

    /**
     * Sorting giving array using quick sort algorithm and without using threads.
     */
    public static <T extends Comparable<? super T>> void sortWithoutThreads(@NotNull T[] array) {
        if (array.length == 0) {
            return;
        }

        sortWithoutThreads(array, 0, array.length - 1);
    }

    /**
     * Sorting giving array using quick sort algorithm and threads.
     */
    public static <T extends Comparable<? super T>> void threadSort(@NotNull T[] array) {
        if (array.length == 0) {
            return;
        }

        if (array.length <= ACTUAL_MAX_LENGTH_FOR_THREADS) {
            sortWithoutThreads(array);
            return;
        }

        (new ForkJoinPool(Runtime.getRuntime().availableProcessors())).invoke(new SortWithThreads<>(array, 0, array.length - 1));
    }

    /**
     * Sorting giving array using quick sort algorithm and threads.
     * Then array length becomes lower than MAX_LENGTH_FOR_THREADS, does not use threads (if not specified, used default
     * value for this).
     */
    /*
    To be honest, I added this only for testing...
     */
    public static <T extends Comparable<? super T>> void threadSort(@NotNull T[] array, int MAX_LENGTH_FOR_THREADS) {
        ACTUAL_MAX_LENGTH_FOR_THREADS = MAX_LENGTH_FOR_THREADS;

        threadSort(array);

        ACTUAL_MAX_LENGTH_FOR_THREADS = DEFAULT_MAX_LENGTH_FOR_THREADS;
    }

    /**
     * Sorting elements with indexes from l to r (inclusive) in given array using quick sort algorithm and without using threads.
     */
    private static <T extends Comparable<? super T>> void sortWithoutThreads(@NotNull T[] array, int l, int r) {
        if (l >= r) {
            return;
        }

        int q = partition(array, l, r);
        sortWithoutThreads(array, l, q);
        sortWithoutThreads(array, q + 1, r);
    }

    /**
     * Choose randomly element x, split array into parts <=x and >x, returns position of x!!!11)))))000 xDDDD
     */
    /*
    Yeap, it's copypaste of https://neerc.ifmo.ru/wiki/index.php?title=%D0%91%D1%8B%D1%81%D1%82%D1%80%D0%B0%D1%8F_%D1%81%D0%BE%D1%80%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%BA%D0%B0
     */
    private static <T extends Comparable<? super T>> int partition(@NotNull T[] array, int l, int r) {
        T v = array[(l + r) / 2];
        int i = l;
        int j = r;
        while (i <= j) {
            while (array[i].compareTo(v) < 0) {
                i++;
            }

            while (array[j].compareTo(v) > 0) {
                j--;
            }

            if (i >= j) {
                break;
            }

            T tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;

            i++;
            j--;
        }

        return j;
    }

    /**
     * Class for performing recursive thread sorting.
     */
    private static class SortWithThreads<T extends Comparable<? super T>> extends RecursiveAction {
        /**
         * Array to sort.
         */
        private final T[] array;

        /**
         * Left bound of array.
         */
        private final int l;

        /**
         * Right bound of array (inclusive).
         */
        private final int r;

        SortWithThreads(T[] array, int l, int r) {
            this.array = array;
            this.l = l;
            this.r = r;
        }

        /**
         * Sorting elements with indexes from l to r (inclusive) in given array using quick sort algorithm and threads.
         */
        @Override
        protected void compute() {
            if (l >= r) {
                return;
            }

            if (r - l + 1 <= ACTUAL_MAX_LENGTH_FOR_THREADS) {
                sortWithoutThreads(array, l, r);
                return;
            }

            int q = partition(array, l, r);
            invokeAll(new SortWithThreads<>(array, l, q), new SortWithThreads<>(array, q + 1, r));
        }
    }
}
