package ru.hse.hashtable;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListTest {
    List a;

    @BeforeEach
    public void initialise() {
        a = new List();
    }

    @Test
    void iteratorEmpty() {
        for (var x : a) {
            fail(); //checking if we never go into cycle body
        }
    }

    @Test
    void iteratorAddAndCheck() {
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");
        int q = 1;
        for (StringPair x : a) {
            assertEquals(q, x.getKey());
            q++;
        }

        assertEquals(4, q);
    }

    @Test
    void emptyEmpty() {
        assertTrue(a.empty());
    }

    @Test
    void emptyNotEmpty() {
        a.push(1, "a");
        assertFalse(a.empty());
        a.push(2, "b");
        a.push(3, "c");
        assertFalse(a.empty());
    }

    @Test
    void emptyAfterRemoving() {
        a.push(1, "a");
        a.remove(1);
        assertTrue(a.empty());
        a.push(2, "b");
        a.push(3, "c");
        a.remove(2);
        a.remove(3);
        assertTrue(a.empty());
        a.push(1, "a");
        assertFalse(a.empty());
        a.remove(1);
        assertTrue(a.empty());
    }

    @Test
    void emptyAfterClear() {
        a.push(1, "a");
        a.clear();
        assertTrue(a.empty());
        a.push(2, "b");
        a.push(3, "c");
        a.clear();
        assertTrue(a.empty());
    }

    @Test
    void findInEmptyList() {
        assertNull(a.get(1));
    }

    @Test
    void findNotExistingObject() {
        a.push(1, "a");
        assertNull(a.get(2));
        a.push(2, "b");
        assertNull(a.get(3));
        a.push(3, "c");
        assertNull(a.get(4));
    }

    @Test
    void findExistingObject() {
        a.push(1, "a");
        assertEquals("a", a.get(1));
        a.push(2, "b");
        assertEquals("a", a.get(1));
        assertEquals("b", a.get(2));
        a.push(3, "c");
        assertEquals("a", a.get(1));
        assertEquals("b", a.get(2));
        assertEquals("c", a.get(3));
    }

    @Test
    void findAfterRemovingObjects() {
        a.push(1, "a");
        a.remove(1);
        assertNull(a.get(1)); //make empty
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");
        a.push(4, "d");
        a.remove(1); //deleting head
        assertNull(a.get(1));
        a.remove(4); //deleting tail
        assertNull(a.get(4));
        a.push(1, "a");
        a.push(4, "d");
        a.remove(3); //deleting in the middle of the List
        assertNull(a.get(3));

        a.clear(); //deleting List
        assertNull(a.get(1));
        assertNull(a.get(2));
        assertNull(a.get(3));
        assertNull(a.get(4));
    }

    @Test
    void contains() { //Not much point in testing that, cause it's just find(), so just a simple test
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");

        assertTrue(a.contains(1));
        assertTrue(a.contains(2));
        assertTrue(a.contains(3));
        assertFalse(a.contains(0));
        assertFalse(a.contains(4));

        a.remove(2);
        assertFalse(a.contains(2));

        a.clear();
        assertFalse(a.contains(1));
    }

    @Test
    void get() { //also just find()
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");

        assertEquals("a", a.get(1));
        assertEquals("b", a.get(2));
        assertEquals("c", a.get(3));
        assertNull(a.get(0));

        a.remove(1);
        assertNull(a.get(1));
        assertEquals("b", a.get(2));

        a.clear();
        assertNull(a.get(3));
    }

    @Test
    void insertExistingKey() { //find() + push(), just simple tests
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");

        a.insert(2, "val1");
        assertEquals("val1", a.get(2));

        a.insert(3, "val2");
        assertEquals("val2", a.get(3));

        a.insert(1, "val3");
        assertEquals("val3", a.get(1));
    }

    @Test
    void insertNewKeys() {
        a.insert(1, "a");
        assertEquals("a", a.get(1));
        a.insert(2, "b");
        assertEquals("b", a.get(2));
        a.insert(3, "c");
        assertEquals("c", a.get(3));
        assertNull(a.get(4));
    }

    @Test
    void insertExistingKeyDosentMakePush() {
        a.insert(1, "a");
        a.insert(1, "b");

        int q = 0;
        for (StringPair x : a) {
            q++;
        }

        assertEquals(1, q);
    }

    @Test
    void push() { //many tests already exists in find(), so just simple test
        a.push(19, "a");
        assertEquals("a", a.get(19));
        a.push(20, "b");
        a.push(1, "c");
        a.remove(19);
        a.remove(20);
        a.push(2, "e");
        a.push(21, "f");
        a.push(3, "g");
        a.remove(21);
        a.push(4, "o");

        int q = 0;
        for (StringPair x : a) {
            q++;
            assertEquals(q, x.getKey());
        }
    }

    @Test
    void removeNotExistingKey() { //many testcase already exists in find and push, so just simple ones
        assertNull(a.remove(0));
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");
        assertNull(a.remove(4));
        a.clear();
        assertNull(a.remove(1));
    }

    @Test
    void removeExistingKeys() {
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");
        assertEquals("a", a.remove(1));
        assertEquals("b", a.remove(2));
        assertEquals("c", a.remove(3));
    }

    @Test
    void removeRepeating() {
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");
        assertEquals("a", a.remove(1));
        assertNull(a.remove(1));
    }

    @Test
    void clearEmptyList() {
        a.clear();
        assertTrue(a.empty());
    }

    @Test
    void clearNotEmptyList() {
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");
        a.clear();
        assertTrue(a.empty());
    }
}