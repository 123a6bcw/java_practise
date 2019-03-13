package ru.hse.hw6.qsort;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class QSortThreadsTest {

    @Test
    void sortWithThreadsOfEmptyArrayDoesNothing() {
        var array = new Integer[0];

        assertDoesNotThrow(() -> QSortThreads.threadSort(array, 0));
    }

    @Test
    void sortWithoutThreadsOfEmptyArrayDoesNothing() {
        var array = new Integer[0];

        assertDoesNotThrow(() -> QSortThreads.sortWithoutThreads(array));
    }

    @Test
    void sortWithThreadsOfOneElementArrayDoesNothing() {
        Integer[] array = {1};

        QSortThreads.threadSort(array, 0);
        assertEquals(1, array[0]);
    }

    @Test
    void sortWithoutThreadsOfOneElementArrayDoesNothing() {
        Integer[] array = {1};

        QSortThreads.sortWithoutThreads(array);
        assertEquals(1, array[0]);
    }

    @Test
    void sortWithThreadsOfSortedArrayDoesNothing() {
        Integer[] arrayToSort = {1,2,3,4,5};
        var arrayResult = arrayToSort.clone();

        QSortThreads.threadSort(arrayToSort, 0);
        assertArrayEquals(arrayResult, arrayToSort);
    }

    @Test
    void sortWithoutThreadsOfSortedArrayDoesNothing() {
        Integer[] arrayToSort = {1,2,3,4,5};
        var arrayResult = arrayToSort.clone();

        QSortThreads.sortWithoutThreads(arrayToSort);
        assertArrayEquals(arrayResult, arrayToSort);
    }

    @Test
    void sortWithThreadsOfUnsortedArraySortsIt() {
        Integer[] arrayToSort = {4,5,1,3,2,6};
        Integer[] arrayResult = {1,2,3,4,5,6};

        QSortThreads.threadSort(arrayToSort, 0);
        assertArrayEquals(arrayResult, arrayToSort);
    }

    @Test
    void sortWithoutThreadsOfUnsortedArraySortsIt() {
        Integer[] arrayToSort = {4,5,1,3,2};
        Integer[] arrayResult = {1,2,3,4,5};

        QSortThreads.sortWithoutThreads(arrayToSort);
        assertArrayEquals(arrayResult, arrayToSort);
    }

    @Test
    void sortWithThreadsOfReverseOrderArraySortsIt() {
        Integer[] arrayToSort = {5,4,3,2,1};
        Integer[] arrayResult = {1,2,3,4,5};

        QSortThreads.threadSort(arrayToSort, 0);
        assertArrayEquals(arrayResult, arrayToSort);
    }

    @Test
    void sortWithoutThreadsOfReverseOrderArraySortsIt() {
        Integer[] arrayToSort = {6,5,4,3,2,1};
        Integer[] arrayResult = {1,2,3,4,5,6};

        QSortThreads.sortWithoutThreads(arrayToSort);
        assertArrayEquals(arrayResult, arrayToSort);
    }

    @Test
    void sortWithThreadsWithRepeatingElementsSortsIt() {
        Integer[] arrayToSort = {1,2,3,1,6,2,1,2,1,1,6,4,6,5,5,4,3,4};
        Integer[] arrayResult = {1,1,1,1,1,2,2,2,3,3,4,4,4,5,5,6,6,6};

        QSortThreads.threadSort(arrayToSort, 0);
        assertArrayEquals(arrayResult, arrayToSort);
    }

    @Test
    void sortWithoutThreadsWithRepeatingElementsSortsIt() {
        Integer[] arrayToSort = {1,2,3,5,1,2,1,4,2,1,1,6,6,5,5,4,3,4};
        Integer[] arrayResult = {1,1,1,1,1,2,2,2,3,3,4,4,4,5,5,5,6,6};

        QSortThreads.sortWithoutThreads(arrayToSort);
        assertArrayEquals(arrayResult, arrayToSort);
    }

    @Test
    void sortWithThreadsOfArrayWithTheSameElementDoesNothing() {
        Integer[] arrayToSort = {1,1,1,1,1,1};
        Integer[] arrayResult = {1,1,1,1,1,1};

        QSortThreads.threadSort(arrayToSort, 0);
        assertArrayEquals(arrayResult, arrayToSort);
    }

    @Test
    void sortWithoutThreadsOfArrayWithTheSameElementDoesNothing() {
        Integer[] arrayToSort = {2,2,2,2};
        Integer[] arrayResult = {2,2,2,2};

        QSortThreads.sortWithoutThreads(arrayToSort);
        assertArrayEquals(arrayResult, arrayToSort);
    }

    /*
    On my machine for random, works better from 100000 and so on.
    For not random works... inadequately strange. I think it's some optimisation magic (Or I'm stupid (probably second one)).
     */
    public static void main(String[] argc) {
        compareAlgorithmsAndPrintMessage(1000);
        compareAlgorithmsAndPrintMessage(10000);
        compareAlgorithmsAndPrintMessage(50000);
        compareAlgorithmsAndPrintMessage(100000);
        compareAlgorithmsAndPrintMessage(200000);
        compareAlgorithmsAndPrintMessage(400000);
        compareAlgorithmsAndPrintMessage(800000);
        compareAlgorithmsAndPrintMessage(2000000);
    }

    /**
     * Describing type of content inside array
     */
    private enum TYPE_OF_CONTENT {
        SORTED, RANDOM, REVERSED
    }

    /**
     * Pair of time for sorting arrays with or without threads.
     */
    private static class TimePair {
        private long timeWithoutThreads;
        private long timeWithThreads;

        private TimePair(long timeWithoutThreads, long timeWithThreads) {
            this.timeWithoutThreads = timeWithoutThreads;
            this.timeWithThreads = timeWithThreads;
        }

        private long getTimeWithoutThreads() {
            return timeWithoutThreads;
        }

        private long getTimeWithThreads() {
            return timeWithThreads;
        }
    }

    /**
     * Compares speed of executing two algorithms and prints corresponding message.
     */
    private static void compareAlgorithmsAndPrintMessage(int arrayLength) {
        TimePair timePairOnSorted = compareAlgorithms(arrayLength, TYPE_OF_CONTENT.SORTED);
        TimePair timePairOnRandom = compareAlgorithms(arrayLength, TYPE_OF_CONTENT.RANDOM);
        TimePair timePairOnReversed = compareAlgorithms(arrayLength, TYPE_OF_CONTENT.REVERSED);

        System.out.println("Array length " + arrayLength);

        printMessage(timePairOnSorted, arrayLength, TYPE_OF_CONTENT.SORTED);
        printMessage(timePairOnRandom, arrayLength, TYPE_OF_CONTENT.RANDOM);
        printMessage(timePairOnReversed, arrayLength, TYPE_OF_CONTENT.REVERSED);
    }

    /**
     * Prints message to System.out about results of the comparison of two algorithms
     */
    private static void printMessage(TimePair timePair, int arrayLength, TYPE_OF_CONTENT type_of_content) {
        double withoutLongerPercent = getPercentLonger(timePair.getTimeWithoutThreads(), timePair.getTimeWithThreads());
        double withLongerPercent = getPercentLonger(timePair.getTimeWithThreads(), timePair.getTimeWithoutThreads());

        if (timePair.getTimeWithThreads() >= timePair.getTimeWithoutThreads()) {
            System.out.println("  Oh NOES! Sort withOUT threads is faster by " + String.format("%.2f", withLongerPercent) + "%!");
        } else {
            System.out.println("  Oh YEAH! Sort WITH threads is faster by " + String.format("%.2f", withoutLongerPercent) + "%!");
        }

        //System.out.println(timePair.getTimeWithoutThreads() + " " + timePair.getTimeWithThreads());
        System.out.print("  Type of the content is ");
        switch (type_of_content) {
            case SORTED:
                System.out.println("sorted\n");
                break;
            case RANDOM:
                System.out.println("random\n");
                break;
            case REVERSED:
                System.out.println("reversed\n");
                break;
        }
    }

    /**
     * Difference in percent of base and target assuming base is higher than target.
     */
    private static double getPercentLonger(long base, long target) {
        return (((double) base) / target) * 100 - 100;
    }

    /**
     * Sorts using two algorithms (with and without threads) and returns results in time.
     */
    private static TimePair compareAlgorithms(int arrayLength, TYPE_OF_CONTENT type_of_content) {
        var random = new Random(arrayLength); //reproducibility!
        var array = new Integer[arrayLength];
        var arrayCopy = new Integer[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            switch (type_of_content) {
                case SORTED:
                    array[i] = i;
                    break;
                case RANDOM:
                    array[i] = random.nextInt();
                    break;
                case REVERSED:
                    array[i] = arrayLength - i - 1;
                    break;
            }

            arrayCopy[i] = array[i];
        }

        long elapsedTimeWithoutThreads = howLongSortsWithoutThreads(array);
        long elapsedTimeWithThreads = howLongSortsWithThreads(arrayCopy);
        return new TimePair(elapsedTimeWithoutThreads, elapsedTimeWithThreads);
    }

    private static <T extends Comparable<? super T>> long howLongSortsWithoutThreads(T[] array) {
        long startTimeWithoutThreads = System.nanoTime();
        QSortThreads.sortWithoutThreads(array);
        //System.out.println(array[7]);
        long endTimeWithoutThreads = System.nanoTime();
        return (endTimeWithoutThreads - startTimeWithoutThreads);
    }

    private static <T extends Comparable<? super T>> long howLongSortsWithThreads(T[] array) {
        long startTimeWithoutThreads = System.nanoTime();
        QSortThreads.threadSort(array);
        //System.out.println(array[5]);
        long endTimeWithoutThreads = System.nanoTime();
        return (endTimeWithoutThreads - startTimeWithoutThreads);
    }
}