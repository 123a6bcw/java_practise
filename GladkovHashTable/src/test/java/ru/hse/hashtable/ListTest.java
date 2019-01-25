package ru.hse.hashtable;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListTest {
    List testList;

    @BeforeEach
    public void initialise() {
        testList = new List();
    }

    @Test
    void iteratorEmpty() {
        for (var x : testList) {
            fail(); //checking if we never go into cycle body
        }
    }

    @Test
    void iteratorAddAndCheck() {
        testList.push(1, "a");
        testList.push(2, "b");
        testList.push(3, "c");
        int q = 1;
        for (StringPair x : testList) {
            assertEquals(q, x.getKey());
            q++;
        }

        assertEquals(4, q);
    }

    @Test
    void emptyEmpty() {
        assertTrue(testList.empty());
    }

    @Test
    void emptyNotEmpty() {
        testList.push(1, "a");
        assertFalse(testList.empty());
        testList.push(2, "b");
        testList.push(3, "c");
        assertFalse(testList.empty());
    }

    @Test
    void emptyAfterRemoving() {
        testList.push(1, "a");
        testList.remove(1);
        assertTrue(testList.empty());
        testList.push(2, "b");
        testList.push(3, "c");
        testList.remove(2);
        testList.remove(3);
        assertTrue(testList.empty());
        testList.push(1, "a");
        assertFalse(testList.empty());
        testList.remove(1);
        assertTrue(testList.empty());
    }

    @Test
    void emptyAfterClear() {
        testList.push(1, "a");
        testList.clear();
        assertTrue(testList.empty());
        testList.push(2, "b");
        testList.push(3, "c");
        testList.clear();
        assertTrue(testList.empty());
    }

    @Test
    void findInEmptyList() {
        assertNull(testList.get(1));
    }

    @Test
    void findNotExistingObject() {
        testList.push(1, "a");
        assertNull(testList.get(2));
        testList.push(2, "b");
        assertNull(testList.get(3));
        testList.push(3, "c");
        assertNull(testList.get(4));
    }

    @Test
    void findExistingObject() {
        testList.push(1, "a");
        assertEquals("a", testList.get(1));
        testList.push(2, "b");
        assertEquals("a", testList.get(1));
        assertEquals("b", testList.get(2));
        testList.push(3, "c");
        assertEquals("a", testList.get(1));
        assertEquals("b", testList.get(2));
        assertEquals("c", testList.get(3));
    }

    @Test
    void findAfterRemovingObjects() {
        testList.push(1, "a");
        testList.remove(1);
        assertNull(testList.get(1)); //make empty
        testList.push(1, "a");
        testList.push(2, "b");
        testList.push(3, "c");
        testList.push(4, "d");
        testList.remove(1); //deleting head
        assertNull(testList.get(1));
        testList.remove(4); //deleting tail
        assertNull(testList.get(4));
        testList.push(1, "a");
        testList.push(4, "d");
        testList.remove(3); //deleting in the middle of the List
        assertNull(testList.get(3));

        testList.clear(); //deleting List
        assertNull(testList.get(1));
        assertNull(testList.get(2));
        assertNull(testList.get(3));
        assertNull(testList.get(4));
    }

    @Test
    void contains() { //Not much point in testing that, cause it's just find(), so just a simple test
        testList.push(1, "a");
        testList.push(2, "b");
        testList.push(3, "c");

        assertTrue(testList.contains(1));
        assertTrue(testList.contains(2));
        assertTrue(testList.contains(3));
        assertFalse(testList.contains(0));
        assertFalse(testList.contains(4));

        testList.remove(2);
        assertFalse(testList.contains(2));

        testList.clear();
        assertFalse(testList.contains(1));
    }

    @Test
    void get() { //also just find()
        testList.push(1, "a");
        testList.push(2, "b");
        testList.push(3, "c");

        assertEquals("a", testList.get(1));
        assertEquals("b", testList.get(2));
        assertEquals("c", testList.get(3));
        assertNull(testList.get(0));

        testList.remove(1);
        assertNull(testList.get(1));
        assertEquals("b", testList.get(2));

        testList.clear();
        assertNull(testList.get(3));
    }

    @Test
    void insertExistingKey() { //find() + push(), just simple tests
        testList.push(1, "a");
        testList.push(2, "b");
        testList.push(3, "c");

        testList.insert(2, "val1");
        assertEquals("val1", testList.get(2));

        testList.insert(3, "val2");
        assertEquals("val2", testList.get(3));

        testList.insert(1, "val3");
        assertEquals("val3", testList.get(1));
    }

    @Test
    void insertNewKeys() {
        testList.insert(1, "a");
        assertEquals("a", testList.get(1));
        testList.insert(2, "b");
        assertEquals("b", testList.get(2));
        testList.insert(3, "c");
        assertEquals("c", testList.get(3));
        assertNull(testList.get(4));
    }

    @Test
    void insertExistingKeyDosentMakePush() {
        testList.insert(1, "a");
        testList.insert(1, "b");

        int q = 0;
        for (StringPair x : testList) {
            q++;
        }

        assertEquals(1, q);
    }

    @Test
    void push() { //many tests already exists in find(), so just simple test
        testList.push(19, "a");
        assertEquals("a", testList.get(19));
        testList.push(20, "b");
        testList.push(1, "c");
        testList.remove(19);
        testList.remove(20);
        testList.push(2, "e");
        testList.push(21, "f");
        testList.push(3, "g");
        testList.remove(21);
        testList.push(4, "o");

        int q = 0;
        for (StringPair x : testList) {
            q++;
            assertEquals(q, x.getKey());
        }
    }

    @Test
    void removeNotExistingKey() { //many testcase already exists in find and push, so just simple ones
        assertNull(testList.remove(0));
        testList.push(1, "a");
        testList.push(2, "b");
        testList.push(3, "c");
        assertNull(testList.remove(4));
        testList.clear();
        assertNull(testList.remove(1));
    }

    @Test
    void removeExistingKeys() {
        testList.push(1, "a");
        testList.push(2, "b");
        testList.push(3, "c");
        assertEquals("a", testList.remove(1));
        assertEquals("b", testList.remove(2));
        assertEquals("c", testList.remove(3));
    }

    @Test
    void removeRepeating() {
        testList.push(1, "a");
        testList.push(2, "b");
        testList.push(3, "c");
        assertEquals("a", testList.remove(1));
        assertNull(testList.remove(1));
    }

    @Test
    void clearEmptyList() {
        testList.clear();
        assertTrue(testList.empty());
    }

    @Test
    void clearNotEmptyList() {
        testList.push(1, "a");
        testList.push(2, "b");
        testList.push(3, "c");
        testList.clear();
        assertTrue(testList.empty());
    }
}