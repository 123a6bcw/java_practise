package ru.mit.spbau.HashTable;

import static org.junit.jupiter.api.Assertions.*;

public class HashTableTest {
    @org.junit.jupiter.api.Test
    public void sizeEmpty() throws Exception {
        HashTable a = new HashTable();
        assertEquals(a.size(), 0);
    }

    @org.junit.jupiter.api.Test
    public void sizeAfterPutting() throws Exception {
        HashTable a = new HashTable();
        a.put("abc", "abc");
        assertEquals(a.size(), 1);
        a.put("b", "b");
        assertEquals(a.size(), 2);
        a.put("c", "c");
        assertEquals(a.size(), 3);
    }

    @org.junit.jupiter.api.Test
    public void sizeAfterPuttingExistingKey() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "a");
        a.put("a", "b");
        assertEquals(a.size(), 1);
        a.put("b", "a");
        a.put("b", "b");
        a.put("a", "a");
        a.put("a", "b");
        assertEquals(a.size(), 2);
    }

    @org.junit.jupiter.api.Test
    public void sizeAfterPuttingAndRemoving() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "a");
        a.put("b", "c");
        a.put("c", "d");
        a.remove("a");
        assertEquals(a.size(), 2);
        a.remove("b");
        assertEquals(a.size(), 1);
        a.remove("c");
        assertEquals(a.size(), 0);
        a.put("a", "a");
        assertEquals(a.size(), 1);
        a.remove("a");
        assertEquals(a.size(), 0);
    }

    @org.junit.jupiter.api.Test
    public void sizeAfterClear() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "b");
        a.put("b", "c");
        a.put("c", "d");
        a.clear();
        assertEquals(a.size(), 0);
    }

    @org.junit.jupiter.api.Test
    public void sizeAfterExpanding() throws Exception {
        HashTable a = new HashTable();
        String s = "a";
        for (int i = 0; i < 32; i++) {
            s += "a";
            a.put(s, s);
            assertEquals(a.size(), i+1);
        }
    }

    @org.junit.jupiter.api.Test
    public void containsInEmptyTable() throws Exception {
        HashTable a = new HashTable();
        assertFalse(a.contains("a"));
    }

    @org.junit.jupiter.api.Test
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

    @org.junit.jupiter.api.Test
    public void containsFalse() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "a");
        a.put("a", "b");
        a.put("b", "c");
        a.put("sasha", "dasha");
        assertFalse(a.contains("masha"));
    }

    @org.junit.jupiter.api.Test
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

    @org.junit.jupiter.api.Test
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

    @org.junit.jupiter.api.Test
    public void get() throws Exception { //same as Contains, so just simple test.
         HashTable a = new HashTable();
         a.put("a", "a");
         a.put("a", "b");
         assertEquals(a.get("a"), "b");
         assertNull(a.get("b"));
         a.remove("a");
         assertNull(a.get("a"));

         a.put("a", "b");
         a.clear();
         assertNull(a.get("a"));
    }

    @org.junit.jupiter.api.Test
    public void getAfterExpanding() throws Exception { //same as Contains, so just simple test.
        HashTable a = new HashTable();
        String s = "a";
        for (int i = 0; i < 32; i++) {
            s = "a";
            a.put(s, s + "b");
        }

        for (int i = 0; i < 32; i++){
            assertEquals(a.get(s), s + "b");
        }
    }
    @org.junit.jupiter.api.Test
    public void putSimple() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "b");
        assertTrue(a.contains("a"));
    }

    @org.junit.jupiter.api.Test
    public void putSameObject() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "b");
        a.put("a", "b");
        assertEquals(a.get("a"), "b");
        assertEquals(a.size(), 1);
    }

    @org.junit.jupiter.api.Test
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
            assertEquals(a.get(s), s + "c");
        }
    }

    @org.junit.jupiter.api.Test
    public void removeInEmptyTable() throws Exception {
        HashTable a = new HashTable();
        assertNull(a.remove("a"));
        assertEquals(a.size(), 0);
    }

    @org.junit.jupiter.api.Test
    public void removeExistingObject() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "b");
        assertEquals(a.remove("a"), "b");
        assertEquals(a.size(), 0);
    }

    @org.junit.jupiter.api.Test
    public void removeNotExistingObject() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "b");
        a.put("b", "c");
        assertNull(a.remove("c"));
        assertEquals(a.size(), 2);
    }

    @org.junit.jupiter.api.Test
    public void clearEmptyTable() throws Exception {
        HashTable a = new HashTable();
        a.clear();
        assertEquals(a.size(), 0);
    }

    @org.junit.jupiter.api.Test
    public void clearBigTable() throws Exception {
        HashTable a = new HashTable();
        a.put("a", "b");
        a.put("b", "c");
        a.put("c", "d");
        a.put("d", "e");
        a.clear();
        assertEquals(a.size(), 0);
    }

    @org.junit.jupiter.api.Test
    public void clearAfterExpanding() throws Exception {
        HashTable a = new HashTable();
        String s = "a";
        for (int i = 0; i < 32; i++) {
            s += "a";
            a.put(s, s);
        }

        a.clear();
        assertEquals(a.size(), 0);
    }
}