package ru.hse.hw3.unbalancedtreeset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class UnbalancedTreeSetTest {
    private UnbalancedTreeSet<Integer> emptySet;
    private UnbalancedTreeSet<Integer> filledSet;
    private Integer[] values = {6,4,2,8};
    private Integer[] sortedValues = {1,2,3,4};
    private Integer[] sortedDescendingValues = {4,3,2,1};


    @BeforeEach
    private void initialise() {
        emptySet  = new UnbalancedTreeSet<>(Comparator.naturalOrder());
        filledSet = new UnbalancedTreeSet<>(Comparator.naturalOrder());

        /*
        Is it okay to initialise that way?
        Cause if add() or remove fails (throws exceptions or something), all my tests would fail.
        And I want to have remove here so I would sure that everything works not just for just created tree.
         */
        filledSet.addAll(Arrays.asList(values));
        filledSet.remove(2);
        filledSet.add(10);
        filledSet.add(5);
        filledSet.remove(10);
        filledSet.remove(5);
        filledSet.add(2);
        filledSet.add(0);
        filledSet.remove(0);
    }

    @Test
    void addAll() {
        assertEquals(4, filledSet.size());
    }

    @Test
    void add() {
        emptySet.add(5);
        emptySet.add(10);
        emptySet.add(12);
        assertEquals(3, emptySet.size());
    }

    @Test
    void addNullThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> emptySet.add(null));
    }

    @Test
    void iteratorOnEmptySet() {
        var iterator = emptySet.iterator();
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorOnFilledSet() {
        var iterator = filledSet.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(4), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(6), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(8), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void descendingIterator() {
        var iterator = filledSet.descendingIterator();
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(8), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(6), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(4), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void sizeOfEmptySetIsZero() {
        assertEquals(0, emptySet.size());
    }

    @Test
    void sizeOfFilledSet() {
        emptySet.add(5);
        assertEquals(1, emptySet.size());
        emptySet.add(6);
        assertEquals(2, emptySet.size());
        assertEquals(4, filledSet.size());
    }

    @Test
    void firstOfEmptySetThrowsNuSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> emptySet.first());
    }

    @Test
    void firstOfFilledSet() {
        assertEquals(Integer.valueOf(2), filledSet.first());
    }

    @Test
    void firstOfOneObjectSet() {
        emptySet.add(5);
        assertEquals(Integer.valueOf(5), emptySet.first());
    }

    @Test
    void lastOfEmptySetReturnsNull() {
        assertThrows(NoSuchElementException.class, () -> emptySet.last());
    }

    @Test
    void LastOfFilledSet() {
        assertEquals(Integer.valueOf(8), filledSet.last());
    }

    @Test
    void LastOfOneObjectSet() {
        emptySet.add(7);
        assertEquals(Integer.valueOf(7), emptySet.last());
    }

    @Test
    void lowerOfEmptySetReturnsNull() {
        assertNull(emptySet.lower(2));
    }

    @Test
    void lowerOnOneElementSet() {
        emptySet.add(2);
        assertNull(emptySet.lower(2));
        assertEquals(2, emptySet.lower(3));
        assertNull(emptySet.lower(1));
    }

    @Test
    void lowerOfNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> filledSet.lower(null));
    }

    @Test
    void lowerOfFilledSet() {
        assertNull(filledSet.lower(1));
        assertNull(filledSet.lower(2));
        for (int i = 3; i <= 8; i += 2) {
            assertEquals(i-1, filledSet.lower(i));
            assertEquals(i-1, filledSet.lower(i + 1));
        }
    }

    @Test
    void floorOfEmptySetReturnsNull() {
        assertNull(emptySet.floor(4));
    }

    @Test
    void floorOnNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> filledSet.floor(null));
    }

    @Test
    void floorOnOneElementSet() {
        emptySet.add(2);
        assertEquals(2, emptySet.floor(2));
        assertEquals(2, emptySet.floor(3));
        assertNull(emptySet.floor(1));
    }

    @Test
    void floorOfFilledSet() {
        assertNull(filledSet.floor(1));
        for (int i = 2; i <= 8; i += 2) {
            assertEquals(i, filledSet.floor(i));
            assertEquals(i, filledSet.floor(i+1));
        }
    }

    @Test
    void ceilingOfEmptySetReturnsNull() {
        assertNull(emptySet.ceiling(3));
    }

    @Test
    void ceilingOnNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> emptySet.ceiling(null));
    }

    @Test
    void CeilingOnOneElementSet() {
        emptySet.add(2);
        assertEquals(2, emptySet.ceiling(1));
        assertEquals(2, emptySet.ceiling(2));
        assertNull(emptySet.ceiling(3));
    }

    @Test
    void ceilingOfFilledSet() {
        assertNull(filledSet.ceiling(9));
        for (int i = 1; i <= 8; i += 2) {
            assertEquals(i+1, filledSet.ceiling(i));
            assertEquals(i+1, filledSet.ceiling(i + 1));
        }
    }

    @Test
    void higherOfEmptySetReturnsNull() {
        assertNull(emptySet.higher(2));
    }

    @Test
    void higherOnOneElementSet() {
        emptySet.add(2);
        assertNull(emptySet.higher(3));
        assertEquals(2, emptySet.higher(1));
        assertNull(emptySet.higher(2));
    }

    @Test
    void higherOfNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> filledSet.higher(null));
    }

    @Test
    void higherOfFilledSet() {
        assertNull(filledSet.higher(8));
        assertNull(filledSet.higher(9));
        for (int i = 7; i >= 3; i -= 2) {
            assertEquals(i+1, filledSet.higher(i));
            assertEquals(i+1, filledSet.higher(i - 1));
        }
    }

    @Test
    void containsInEmptySetReturnsFalse() {
        assertFalse(emptySet.contains(0));
    }

    @Test
    void containsInOneElementSet() {
        emptySet.add(4);
        assertTrue(emptySet.contains(4));
    }

    @Test
    void containsInFilledSet() {
        for (int i = 2; i <= 8; i += 2) {
            assertTrue(filledSet.contains(i));
        }
    }

    @Test
    void clearThrowsUnsupported() {
    }

    @Test
    void removeThrowsUnsupported() {
    }

    @Test
    void removeAllThrowsUnsupported() {
    }

    @Test
    void descendingSet() {

    }
}