package ru.hse.hw6.qsort;

import org.jetbrains.annotations.NotNull;

/**
 * Class for sorting arrays using quick sort algorithm and either using threads or not.
 */
public class QSortThreads {
    /**
     * Sorting giving array using quick sort algorithm and without using threads.
     */
    public static <T extends Comparable<T>> void sortWithoutThreads(@NotNull T[] array) {
        if (array.length == 0) {
            return;
        }

        sortWithoutThreads(array, 0, array.length - 1);
    }

    /**
     * Sorting giving array using quick sort algorithm and threads.
     */
    public static <T extends Comparable<T>> void threadSort(T[] array) {
        if (array.length == 0) {
            return;
        }

        threadSort(array, 0, array.length - 1);
    }

    /**
     * Sorting elements with indexes from l to r in given array using quick sort algorithm and without using threads.
     */
    private static <T extends Comparable<T>> void sortWithoutThreads(T[] array, int l, int r) {
        if (l > r) {
            return;
        }

        int q = partition(array, l, r);
        sortWithoutThreads(array, l, q);
        sortWithoutThreads(array, q + 1, r);
    }

    /**
     * Sorting elements with indexes from l to r in given array using quick sort algorithm and threads.
     */
    private static <T extends Comparable<T>> void threadSort(T[] array, int l, int r) {
        if (l > r) {
            return;
        }

        int q = partition(array, l, r);
        threadSort(array, l, q); //TODO add multithread
        threadSort(array, q + 1, r);
    }

    /**
     * Choose randomly element x, split array into parts <x, =x and >x, returns position of x!!!11)))))000 xDDDD
     */
    private static <T extends Comparable<T>> int partition(T[] array, int l, int r) {
        return 0;
    }
}
