package ru.hse.hw6.qsort;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QSortThreadsTest {

    @Test
    void sortWithThreadsOfEmptyArrayDoesNothing() {
        var array = new Integer[0];

        assertDoesNotThrow(() -> QSortThreads.threadSort(array));
    }

    @Test
    void sortWithoutThreadsOfEmptyArrayDoesNothing() {
        var array = new Integer[0];

        assertDoesNotThrow(() -> QSortThreads.sortWithoutThreads(array));
    }

    @Test
    void sortWithThreadsOfOneElementArrayDoesNothing() {
        Integer[] array = {1};

        QSortThreads.threadSort(array);
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

        QSortThreads.threadSort(arrayToSort);
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

        QSortThreads.threadSort(arrayToSort);
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

        QSortThreads.threadSort(arrayToSort);
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

        QSortThreads.threadSort(arrayToSort);
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

        QSortThreads.threadSort(arrayToSort);
        assertArrayEquals(arrayResult, arrayToSort);
    }

    @Test
    void sortWithoutThreadsOfArrayWithTheSameElementDoesNothing() {
        Integer[] arrayToSort = {2,2,2,2};
        Integer[] arrayResult = {2,2,2,2};

        QSortThreads.sortWithoutThreads(arrayToSort);
        assertArrayEquals(arrayResult, arrayToSort);
    }
}