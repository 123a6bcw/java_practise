package ru.hse.hw3.unbalancedtreeset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class UnbalancedTreeSetTest {
    private UnbalancedTreeSet<Integer> emptySet;
    private UnbalancedTreeSet<Integer> filledSet;
    private Integer[] values = {6,4,2,8,16,10,0,12,14};

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
        filledSet.add(22);
        filledSet.add(5);
        filledSet.remove(22);
        filledSet.remove(5);
        filledSet.add(2);
        filledSet.add(-1);
        filledSet.remove(-1);
    }

    @Test
    void addAll() {
        assertEquals(9, filledSet.size());
    }

    @Test
    void add() {
        emptySet.add(5);
        emptySet.add(10);
        emptySet.add(12);
        assertEquals(3, emptySet.size());
    }

    @Test
    void addExistingObject() {
        filledSet.add(6);
        assertEquals(9, filledSet.size());
    }

    @Test
    void addRemovedObject() {
        filledSet.remove(6);
        filledSet.add(6);
        assertEquals(9, filledSet.size());
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
        for (int i = 0; i <= 16; i += 2) {
            assertTrue(iterator.hasNext());
            assertEquals(i, iterator.next());
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    void iteratorInvalidatesAfterModifications() {
        var iterator = filledSet.iterator();
        filledSet.add(1);
        assertThrows(ConcurrentModificationException.class, iterator::next);
        iterator = filledSet.iterator();
        filledSet.remove(6);
        assertThrows(ConcurrentModificationException.class, iterator::next);
        iterator = emptySet.iterator();
        emptySet.add(1);
        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    void iteratorInvalidatesAfterClear() {
        var iterator = filledSet.iterator();
        filledSet.clear();
        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    void iteratorDoesNotInvalidatesIfObjectWasntRemovedOrAddWithNoEffect() {
        var iterator = filledSet.iterator();
        filledSet.add(2);
        assertDoesNotThrow(iterator::next);

        filledSet.remove(1);
        assertDoesNotThrow(iterator::next);
    }

    @Test
    void iteratorInvalidatesAfterClearWithNoEffect() {
        var iterator = emptySet.iterator();
        emptySet.clear();
        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    void descendingIterator() {
        var iterator = filledSet.descendingIterator();
        for (int i = 16; i >= 0; i -= 2) {
            assertTrue(iterator.hasNext());
            assertEquals(i, iterator.next());
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    void descendingIteratorInvalidation() {
        var iterator = filledSet.descendingIterator();
        filledSet.add(1);
        assertThrows(ConcurrentModificationException.class, iterator::next);
        iterator = filledSet.descendingIterator();
        filledSet.remove(6);
        assertThrows(ConcurrentModificationException.class, iterator::next);
        iterator = emptySet.descendingIterator();
        emptySet.add(1);
        assertThrows(ConcurrentModificationException.class, iterator::next);

        iterator = filledSet.descendingIterator();
        filledSet.add(2);
        assertDoesNotThrow(iterator::next);

        filledSet.remove(22);
        assertDoesNotThrow(iterator::next);
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
        assertEquals(9, filledSet.size());
    }

    @Test
    void creatingNonSortableElementsCrashes() {
        var crashSet = new UnbalancedTreeSet<UnbalancedTreeSet<Object>>();
        assertDoesNotThrow(() -> crashSet.add(new UnbalancedTreeSet<>())); //No elements are being compare, nothing to crash
        assertThrows(ClassCastException.class, () -> crashSet.remove(new UnbalancedTreeSet<>()));
    }

    @Test
    void creatingNullComparatorCrashes() {
        assertThrows(IllegalArgumentException.class, () -> new UnbalancedTreeSet<Integer>(null));
    }

    @Test
    void firstOfEmptySetThrowsNuSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> emptySet.first());
    }

    @Test
    void firstOfFilledSet() {
        assertEquals(0, filledSet.first());
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
        assertEquals(16, filledSet.last());
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
        assertNull(filledSet.lower(-1));
        assertNull(filledSet.lower(0));
        for (int i = 1; i <= 16; i += 2) {
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
        assertNull(filledSet.floor(-1));
        for (int i = 0; i <= 16; i += 2) {
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
        assertNull(filledSet.ceiling(17));
        for (int i = -1; i <= 16; i += 2) {
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
        assertNull(filledSet.higher(16));
        assertNull(filledSet.higher(17));
        for (int i = 15; i >= -1; i -= 2) {
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
    void sizeOfEmptySet() {
        assertEquals(0, emptySet.size());
    }

    @Test
    void sizeAfterAddsAndRemoving() {
        assertEquals(9, filledSet.size());
        filledSet.add(10);
        filledSet.add(12);
        assertEquals(9, filledSet.size());
        filledSet.remove(4);
        assertEquals(8, filledSet.size());
        for (int i = 2; i <= 16; i++) {
            filledSet.remove(i);
        }
        assertEquals(1, filledSet.size());
        filledSet.remove(0);
        assertEquals(0, filledSet.size());
    }

    @Test
    void clearOfEmptySet() {
        emptySet.clear();
        assertEquals(0, emptySet.size());
    }

    @Test
    void clearOfFilledSet() {
        filledSet.clear();
        assertEquals(0, filledSet.size());
    }

    @Test
    void removeInEmptySetDoesNothing() {
        assertDoesNotThrow(() -> emptySet.remove(45));
    }

    @Test
    void removeInOneObjectSetMakesItClear() {
        emptySet.add(5);
        emptySet.remove(5);
        assertEquals(0, emptySet.size());
    }

    @Test
    void descendingSetOfDescendingSetIsTheSameObject() {
        assertSame(emptySet, emptySet.descendingSet().descendingSet());
        assertSame(filledSet, filledSet.descendingSet().descendingSet());
    }

    @Test
    void descendingSetHaveTheSameElementsInDescendingOrder() {
        assertEquals(0, emptySet.descendingSet().size());
        assertEquals(9, filledSet.descendingSet().size());

        var iterator = filledSet.iterator();
        var iteratorDescending = filledSet.descendingSet().descendingIterator();

        while (iterator.hasNext()) {
            assertEquals(iterator.next(), iteratorDescending.next());
        }

        assertFalse(iteratorDescending.hasNext());
    }

    @Test
    void descendingSetChangesReflectsAnotherSet() {
        var descendingSet = filledSet.descendingSet();

        filledSet.remove(6);
        assertFalse(descendingSet.contains(6));

        filledSet.add(10);
        assertTrue(descendingSet.contains(10));

        descendingSet.add(0);
        assertTrue(filledSet.contains(0));

        descendingSet.remove(0);
        assertFalse(filledSet.contains(0));

        descendingSet.clear();
        assertEquals(0, filledSet.size());
        assertEquals(0, descendingSet.size());
    }

    @Test
    void descendingSetLowerIsOppositeToHigher() {
        for (int i = 0; i <= 10; i++) {
            assertEquals(filledSet.lower(i), filledSet.descendingSet().higher(i));
            assertEquals(emptySet.lower(i), emptySet.descendingSet().higher(i));

            assertEquals(filledSet.descendingSet().lower(i), filledSet.higher(i));
            assertEquals(emptySet.descendingSet().lower(i), emptySet.higher(i));
        }
    }

    @Test
    void descendingSetFloorIsOppositeToCeiling() {
        for (int i = 0; i <= 10; i++) {
            assertEquals(filledSet.floor(i), filledSet.descendingSet().ceiling(i));
            assertEquals(emptySet.floor(i), emptySet.descendingSet().ceiling(i));

            assertEquals(filledSet.descendingSet().floor(i), filledSet.ceiling(i));
            assertEquals(emptySet.descendingSet().floor(i), emptySet.ceiling(i));
        }
    }

    @Test
    void descendingSetFirstIsOppositeToLast() {
        assertEquals(filledSet.first(), filledSet.descendingSet().last());
        assertThrows(NoSuchElementException.class, () -> emptySet.descendingSet().last());

        assertEquals(filledSet.descendingSet().first(), filledSet.last());
        assertThrows(NoSuchElementException.class, () -> emptySet.descendingSet().first());
    }
}