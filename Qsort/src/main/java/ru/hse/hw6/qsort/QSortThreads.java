package ru.hse.hw6.qsort;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;

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
     * Sorting elements with indexes from l to r (inclusive) in given array using quick sort algorithm and without using threads.
     */
    private static <T extends Comparable<T>> void sortWithoutThreads(T[] array, int l, int r) {
        if (l >= r) {
            return;
        }

        int q = partition(array, l, r);
        sortWithoutThreads(array, l, q);
        sortWithoutThreads(array, q + 1, r);
    }

    /**
     * Sorting elements with indexes from l to r (inclusive) in given array using quick sort algorithm and threads.
     */
    private static <T extends Comparable<T>> void threadSort(T[] array, int l, int r){
        if (l >= r) {
            return;
        }

        int q = partition(array, l, r);
        var leftThread = new Thread(() -> threadSort(array, l, q));

        T[] rightArrayCopy = Arrays.copyOfRange(array, q + 1, r + 1);
        var rightThread = new Thread(() -> threadSort(rightArrayCopy, 0, rightArrayCopy.length - 1));

        try {
            leftThread.start();
            rightThread.start();
            leftThread.join();
            rightThread.join();
        } catch (InterruptedException e) {
            System.exit(1);
        }

        System.arraycopy(rightArrayCopy, 0, array, q + 1, r - q);
    }

    /**
     * Choose randomly element x, split array into parts <=x and >x, returns position of x!!!11)))))000 xDDDD
     */
    /*
    Yeap, it's copypaste of https://neerc.ifmo.ru/wiki/index.php?title=%D0%91%D1%8B%D1%81%D1%82%D1%80%D0%B0%D1%8F_%D1%81%D0%BE%D1%80%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%BA%D0%B0
     */
    private static <T extends Comparable<T>> int partition(T[] array, int l, int r) {
        T v = array[(l + r) / 2]; // Could be random, but, like, whatever.
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
}
