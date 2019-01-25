package ru.hse.hashtable;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HashTableTest {
    HashTable a;

    @BeforeEach
    public void initialise() {
        a = new HashTable();
    }

    @Test
    public void sizeEmpty() {
        assertEquals(0, a.size());
    }

    @Test
    public void sizeAfterPutting() {
        a.put("abc", "abc");
        assertEquals(1, a.size());
        a.put("b", "b");
        assertEquals(2, a.size());
        a.put("c", "c");
        assertEquals(3, a.size());
    }

    @Test
    public void sizeAfterPuttingExistingKey() {
        HashTable a = new HashTable();
        a.put("a", "a");
        a.put("a", "b");
        assertEquals(1, a.size());
        a.put("b", "a");
        a.put("b", "b");
        a.put("a", "a");
        a.put("a", "b");
        assertEquals(2, a.size());
    }

    @Test
    public void sizeAfterPuttingAndRemoving() {
        a.put("a", "a");
        a.put("b", "c");
        a.put("c", "d");
        a.remove("a");
        assertEquals(2, a.size());
        a.remove("b");
        assertEquals(1, a.size());
        a.remove("c");
        assertEquals(0, a.size());
        a.put("a", "a");
        assertEquals(1, a.size());
        a.remove("a");
        assertEquals(0, a.size());
    }

    @Test
    public void sizeAfterClear() {
        a.put("a", "b");
        a.put("b", "c");
        a.put("c", "d");
        a.clear();
        assertEquals(0, a.size());
    }

    @Test
    public void sizeAfterExpanding() {
        String s = "a";
        for (int i = 0; i < 32; i++) {
            s += "a";
            a.put(s, s);
            assertEquals(i + 1, a.size());
        }
    }

    @Test
    public void containsInEmptyTable() {
        assertFalse(a.contains("a"));
    }

    @Test
    public void containsTrue() {
        a.put("a", "a");
        a.put("a", "b");
        a.put("b", "c");
        a.put("sasha", "dasha");
        assertTrue(a.contains("a"));
        assertTrue(a.contains("b"));
        assertTrue(a.contains("sasha"));
    }

    @Test
    public void containsFalse() {
        a.put("a", "a");
        a.put("a", "b");
        a.put("b", "c");
        a.put("sasha", "dasha");
        assertFalse(a.contains("masha"));
    }

    @Test
    public void containsAfterRemoving() {
        a.put("a", "a");
        a.put("a", "b");
        a.put("b", "c");
        a.put("sasha", "dasha");
        a.remove("a");
        a.remove("b");
        assertFalse(a.contains("a"));
        assertFalse(a.contains("b"));
    }

    @Test
    public void containsAfterClear() {
        a.put("a", "a");
        a.put("a", "b");
        a.put("b", "c");
        a.put("sasha", "dasha");
        a.clear();
        assertFalse(a.contains("a"));
        assertFalse(a.contains("b"));
    }

    @Test
    public void get() { //same as Contains, so just simple test.
        a.put("a", "a");
        a.put("a", "b");
        assertEquals("b", a.get("a"));
        assertNull(a.get("b"));
        a.remove("a");
        assertNull(a.get("a"));

        a.put("a", "b");
        a.clear();
        assertNull(a.get("a"));
    }

    @Test
    public void getAfterExpanding() { //same as Contains, so just simple test.
        String s = "a";
        for (int i = 0; i < 32; i++) {
            s = "a";
            a.put(s, s + "b");
        }

        for (int i = 0; i < 32; i++){
            assertEquals(s + "b", a.get(s));
        }
    }

    @Test
    public void putSimple() {
        a.put("a", "b");
        assertTrue(a.contains("a"));
    }

    @Test
    public void putSameObject() {
        a.put("a", "b");
        a.put("a", "b");
        assertEquals("b", a.get("a"));
        assertEquals(1, a.size());
    }

    @Test
    public void putSameObjectAfterExpanding() {
        String s = "a";
        for (int i = 0; i < 32; i++) {
            s += "a";
            a.put(s, s + "b");
        }

        s = "a";
        for (int i = 0; i < 32; i++) {
            s += "a";
            a.put(s, s + "c");
            assertEquals(s + "c", a.get(s));
        }
    }

    @Test
    public void removeInEmptyTable() {
        assertNull(a.remove("a"));
        assertEquals(0, a.size());
    }

    @Test
    public void removeExistingObject() {
        a.put("a", "b");
        assertEquals("b", a.remove("a"));
        assertEquals(0, a.size());
    }

    @Test
    public void removeNotExistingObject() {
        a.put("a", "b");
        a.put("b", "c");
        assertNull(a.remove("c"));
        assertEquals(2, a.size());
    }

    @Test
    public void clearEmptyTable() {
        a.clear();
        assertEquals(0, a.size());
    }

    @Test
    public void clearBigTable() {
        a.put("a", "b");
        a.put("b", "c");
        a.put("c", "d");
        a.put("d", "e");
        a.clear();
        assertEquals(0, a.size());
    }

    @Test
    public void clearAfterExpanding() {
        String s = "a";
        for (int i = 0; i < 32; i++) {
            s += "a";
            a.put(s, s);
        }

        a.clear();
        assertEquals(0, a.size());
    }
}