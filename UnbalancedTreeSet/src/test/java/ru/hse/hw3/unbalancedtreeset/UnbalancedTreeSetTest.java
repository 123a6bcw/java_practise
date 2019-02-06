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
    private Integer[] values = {1,5,6,2};
    private Integer[] sortedValues = {1,2,5,6};
    private Integer[] sortedDescendingValues = {6,5,2,1};


    @BeforeEach
    private void initialise() {
        emptySet  = new UnbalancedTreeSet<>(Comparator.naturalOrder());
        filledSet = new UnbalancedTreeSet<>(Comparator.naturalOrder());
        filledSet.addAll(Arrays.asList(values));
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
        assertEquals(Integer.valueOf(1), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(5), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(6), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void descendingIterator() {
        var iterator = filledSet.descendingIterator();
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(6), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(5), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(1), iterator.next());
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
        assertEquals(Integer.valueOf(1), filledSet.first());
    }

    @Test
    void firstOfOneObjectSet() {
        emptySet.add(5);
        assertEquals(Integer.valueOf(5), filledSet.first());
    }

    @Test
    void lastOfEmptySetReturnsNull() {
        assertThrows(NoSuchElementException.class, () -> emptySet.last());
    }

    @Test
    void LastOfFilledSet() {
        assertEquals(Integer.valueOf(6), filledSet.last());
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
    void lowerOfFilledSet() {
        assertEquals(Integer.valueOf(1), filledSet.lower(2));
    }

    @Test
    void floorOfEmptySetReturnsNull() {
        assertNull(emptySet.floor(4));
    }

    @Test
    void floorOfFilledSet() {
    }

    @Test
    void ceilingOfEmptySetReturnsNull() {
        assertNull(emptySet.ceiling(3));
    }

    @Test
    void ceilingOfFilledSet() {
    }

    @Test
    void higherOfEmptySetReturnsNull() {
        assertNull(emptySet.higher(3));
    }

    @Test
    void higherOfFilledSet() {
    }

    @Test
    void containsInEmptySetReturnsFalse() {
    }

    @Test
    void containsInFilledSet() {
        assertTrue(filledSet.contains(6));
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
    void retainAllThrowsUnsupported() {
    }

    @Test
    void descendingSet() {

    }
}