package ru.mit.spbau.HashTable;

import static org.junit.jupiter.api.Assertions.*;

class ListTest {
    @org.junit.jupiter.api.Test
    void iteratorEmpty() {
        List a = new List();
        for (Node x : a) {
            assertTrue(false); //checking if we never go into cycle body
        }
    }

    @org.junit.jupiter.api.Test
    void iteratorAddAndCheck() {
        List a = new List();
        a.push(new Node(1, "a"));
        a.push(new Node(2, "b"));
        a.push(new Node(3, "c"));
        int q = 1;
        for (Node x : a) {
            assertEquals(x.key, q);
            q++;
        }

        assertEquals(q, 4);
    }

    @org.junit.jupiter.api.Test
    void emptyEmpty() {
        List a = new List();
        assertTrue(a.empty());
    }

    @org.junit.jupiter.api.Test
    void emptyNotEmpty() {
        List a = new List();
        a.push(1, "a");
        assertFalse(a.empty());
        a.push(2, "b");
        a.push(3, "c");
        assertFalse(a.empty());
    }

    @org.junit.jupiter.api.Test
    void emptyAfterRemoving() {
        List a = new List();
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

    @org.junit.jupiter.api.Test
    void emptyAfterClear() {
        List a = new List();
        a.push(1, "a");
        a.clear();
        assertTrue(a.empty());
        a.push(2, "b");
        a.push(3, "c");
        a.clear();
        assertTrue(a.empty());
    }

    @org.junit.jupiter.api.Test
    void findInEmptyList() {
        List a = new List();
        assertNull(a.find(1));
    }

    @org.junit.jupiter.api.Test
    void findNotExistingObject() {
        List a = new List();
        a.push(1, "a");
        assertNull(a.find(2));
        a.push(2, "b");
        assertNull(a.find(3));
        a.push(3, "c");
        assertNull(a.find(4));
    }

    @org.junit.jupiter.api.Test
    void findExistingObject() {
        List a = new List();
        a.push(1, "a");
        assertEquals(a.find(1).value, "a");
        a.push(2, "b");
        assertEquals(a.find(1).value, "a");
        assertEquals(a.find(2).value, "b");
        a.push(3, "c");
        assertEquals(a.find(1).value, "a");
        assertEquals(a.find(2).value, "b");
        assertEquals(a.find(3).value, "c");
    }

    @org.junit.jupiter.api.Test
    void findAfterRemovingObjects() {
        List a = new List();
        a.push(1, "a");
        a.remove(1);
        assertNull(a.find(1)); //make empty
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");
        a.push(4, "d");
        a.remove(1); //deleting head
        assertNull(a.find(1));
        a.remove(4); //deleting tail
        assertNull(a.find(4));
        a.push(1, "a");
        a.push(4, "d");
        a.remove(3); //deleting in the middle of the List
        assertNull(a.find(3));

        a.clear(); //deleting List
        assertNull(a.find(1));
        assertNull(a.find(2));
        assertNull(a.find(3));
        assertNull(a.find(4));
    }

    @org.junit.jupiter.api.Test
    void contains() { //Not much point in testing that, cause it's just find(), so just a simple test
        List a = new List();
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

    @org.junit.jupiter.api.Test
    void get() { //also just find()
        List a = new List();
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");

        assertEquals(a.get(1), "a");
        assertEquals(a.get(2), "b");
        assertEquals(a.get(3), "c");
        assertNull(a.get(0));

        a.remove(1);
        assertNull(a.get(1));
        assertEquals(a.get(2), "b");

        a.clear();
        assertNull(a.get(3));
    }

    @org.junit.jupiter.api.Test
    void insertExistingKey() { //find() + push(), just simple tests
        List a = new List();
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");

        a.insert(2, "babushka");
        assertEquals(a.get(2), "babushka");

        a.insert(3, "dedushka");
        assertEquals(a.get(3), "dedushka");

        a.insert(1, "repka");
        assertEquals(a.get(1), "repka");
    }

    @org.junit.jupiter.api.Test
    void insertNewKeys() {
        List a = new List();
        a.insert(1, "a");
        assertEquals(a.get(1), "a");
        a.insert(2, "b");
        assertEquals(a.get(2), "b");
        a.insert(3, "c");
        assertEquals(a.get(3), "c");
        assertNull(a.get(4));
    }

    @org.junit.jupiter.api.Test
    void insertExistingKeyDosentMakePush() {
        List a = new List();
        a.insert(1, "a");
        a.insert(1, "b");

        int q = 0;
        for (Node x : a) {
            q++;
        }

        assertEquals(q, 1);
    }

    @org.junit.jupiter.api.Test
    void push() { //many tests already exists in find(), so just simple test
        List a = new List();
        a.push(19, "a");
        assertEquals(a.find(19).key, 19);
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
        for (Node x : a) {
            q++;
            assertEquals(x.key, q);
        }
    }

    @org.junit.jupiter.api.Test
    void push1() { //same push(), previous one just creating a new Node himself
        List a = new List();
        a.push(new Node(19, "a"));
        assertEquals(a.find(19).key, 19);
        a.push(new Node(20, "b"));
        a.push(new Node(1, "c"));
        a.remove(19);
        a.remove(20);
        a.push(new Node(2, "e"));
        a.push(new Node(21, "f"));
        a.push(new Node(3, "g"));
        a.remove(21);
        a.push(new Node(4, "o"));

        int q = 0;
        for (Node x : a) {
            q++;
            assertEquals(x.key, q);
        }
    }

    @org.junit.jupiter.api.Test
    void removeNotExistingKey() { //many testcase already exists in find and push, so just simple ones
        List a = new List();
        assertNull(a.remove(0));
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");
        assertNull(a.remove(4));
        a.clear();
        assertNull(a.remove(1));
    }

    @org.junit.jupiter.api.Test
    void removeExistingKeys() {
        List a = new List();
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");
        assertEquals(a.remove(1), "a");
        assertEquals(a.remove(2), "b");
        assertEquals(a.remove(3), "c");
    }

    @org.junit.jupiter.api.Test
    void removeRepeating() {
        List a = new List();
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");
        assertEquals(a.remove(1), "a");
        assertNull(a.remove(1));
    }

    @org.junit.jupiter.api.Test
    void clearEmptyList() {
        List a = new List();
        a.clear();
        assertTrue(a.empty());
    }

    @org.junit.jupiter.api.Test
    void clearNotEmptyList() {
        List a = new List();
        a.push(1, "a");
        a.push(2, "b");
        a.push(3, "c");
        a.clear();
        assertTrue(a.empty());
    }

}