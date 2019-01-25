package ru.hse.hashtable;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class HashTableTest {
    @Test
    public void sizeEmpty() throws Exception {
        HashTable a = new HashTable();
        assertEquals(0, a.size());
    }

    @Test
    public void sizeAfterPutting() throws Exception {
        HashTable a = new HashTable();
        a.put("abc", "abc");
        assertEquals(1, a.size());
        a.put("b", "b");
        assertEquals(2, a.size());
        a.put("c", "c");
        assertEquals(3, a.size());
    }

    @Test
    public void sizeAfterPuttingExistingKey() throws Exception {
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
    public void sizeAfterPuttingAndRemoving() throws Exception {
        HashTable a = new HashTable();
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
    public void sizeAfterClear() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "b");
        a.put("b", "c");
        a.put("c", "d");
        a.clear();
        assertEquals(0, a.size());
    }

    @Test
    public void sizeAfterExpanding() throws Exception {
        HashTable a = new HashTable();
        String s = "a";
        for (int i = 0; i < 32; i++) {
            s += "a";
            a.put(s, s);
            assertEquals(i + 1, a.size());
        }
    }

    @Test
    public void containsInEmptyTable() throws Exception {
        HashTable a = new HashTable();
        assertFalse(a.contains("a"));
    }

    @Test
    public void containsTrue() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "a");
        a.put("a", "b");
        a.put("b", "c");
        a.put("sasha", "dasha");
        assertTrue(a.contains("a"));
        assertTrue(a.contains("b"));
        assertTrue(a.contains("sasha"));
    }

    @Test
    public void containsFalse() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "a");
        a.put("a", "b");
        a.put("b", "c");
        a.put("sasha", "dasha");
        assertFalse(a.contains("masha"));
    }

    @Test
    public void containsAfterRemoving() throws Exception {
        HashTable a = new HashTable();
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
    public void containsAfterClear() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "a");
        a.put("a", "b");
        a.put("b", "c");
        a.put("sasha", "dasha");
        a.clear();
        assertFalse(a.contains("a"));
        assertFalse(a.contains("b"));
    }

    @Test
    public void get() throws Exception { //same as Contains, so just simple test.
        HashTable a = new HashTable();
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
    public void getAfterExpanding() throws Exception { //same as Contains, so just simple test.
        HashTable a = new HashTable();
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
    public void putSimple() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "b");
        assertTrue(a.contains("a"));
    }

    @Test
    public void putSameObject() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "b");
        a.put("a", "b");
        assertEquals("b", a.get("a"));
        assertEquals(1, a.size());
    }

    @Test
    public void putSameObjectAfterExpanding() throws Exception {
        HashTable a = new HashTable();
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
    public void removeInEmptyTable() throws Exception {
        HashTable a = new HashTable();
        assertNull(a.remove("a"));
        assertEquals(0, a.size());
    }

    @Test
    public void removeExistingObject() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "b");
        assertEquals("b", a.remove("a"));
        assertEquals(0, a.size());
    }

    @Test
    public void removeNotExistingObject() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "b");
        a.put("b", "c");
        assertNull(a.remove("c"));
        assertEquals(2, a.size());
    }

    @Test
    public void clearEmptyTable() throws Exception {
        HashTable a = new HashTable();
        a.clear();
        assertEquals(0, a.size());
    }

    @Test
    public void clearBigTable() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "b");
        a.put("b", "c");
        a.put("c", "d");
        a.put("d", "e");
        a.clear();
        assertEquals(0, a.size());
    }

    @Test
    public void clearAfterExpanding() throws Exception {
        HashTable a = new HashTable();
        String s = "a";
        for (int i = 0; i < 32; i++) {
            s += "a";
            a.put(s, s);
        }

        a.clear();
        assertEquals(0, a.size());
    }
}