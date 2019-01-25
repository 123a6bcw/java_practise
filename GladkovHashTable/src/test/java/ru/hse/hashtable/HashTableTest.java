package ru.hse.hashtable;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HashTableTest {
    private HashTable testTable;

    @BeforeEach
    public void initialise() {
        testTable = new HashTable();
    }

    @Test
    public void sizeEmpty() {
        assertEquals(0, testTable.size());
    }

    @Test
    public void SizeAfterPutting() {
        testTable.put("abc", "abc");
        assertEquals(1, testTable.size());
        testTable.put("b", "b");
        assertEquals(2, testTable.size());
        testTable.put("c", "c");
        assertEquals(3, testTable.size());
    }

    @Test
    public void sizeAfterPuttingExistingKey() {
        HashTable a = new HashTable();
        a.put("testTable", "testTable");
        a.put("testTable", "b");
        assertEquals(1, a.size());
        a.put("b", "testTable");
        a.put("b", "b");
        a.put("testTable", "testTable");
        a.put("testTable", "b");
        assertEquals(2, a.size());
    }

    @Test
    public void sizeAfterPuttingAndRemoving() {
        testTable.put("testTable", "testTable");
        testTable.put("b", "c");
        testTable.put("c", "d");
        testTable.remove("testTable");
        assertEquals(2, testTable.size());
        testTable.remove("b");
        assertEquals(1, testTable.size());
        testTable.remove("c");
        assertEquals(0, testTable.size());
        testTable.put("testTable", "testTable");
        assertEquals(1, testTable.size());
        testTable.remove("testTable");
        assertEquals(0, testTable.size());
    }

    @Test
    public void sizeAfterClear() {
        testTable.put("testTable", "b");
        testTable.put("b", "c");
        testTable.put("c", "d");
        testTable.clear();
        assertEquals(0, testTable.size());
    }

    @Test
    public void sizeAfterExpanding() {
        String s = "testTable";
        for (int i = 0; i < 32; i++) {
            s += "testTable";
            testTable.put(s, s);
            assertEquals(i + 1, testTable.size());
        }
    }

    @Test
    public void containsInEmptyTable() {
        assertFalse(testTable.contains("testTable"));
    }

    @Test
    public void containsTrue() {
        testTable.put("testTable", "testTable");
        testTable.put("testTable", "b");
        testTable.put("b", "c");
        testTable.put("sasha", "dasha");
        assertTrue(testTable.contains("testTable"));
        assertTrue(testTable.contains("b"));
        assertTrue(testTable.contains("sasha"));
    }

    @Test
    public void containsFalse() {
        testTable.put("testTable", "testTable");
        testTable.put("testTable", "b");
        testTable.put("b", "c");
        testTable.put("sasha", "dasha");
        assertFalse(testTable.contains("masha"));
    }

    @Test
    public void containsAfterRemoving() {
        testTable.put("testTable", "testTable");
        testTable.put("testTable", "b");
        testTable.put("b", "c");
        testTable.put("sasha", "dasha");
        testTable.remove("testTable");
        testTable.remove("b");
        assertFalse(testTable.contains("testTable"));
        assertFalse(testTable.contains("b"));
    }

    @Test
    public void containsAfterClear() {
        testTable.put("testTable", "testTable");
        testTable.put("testTable", "b");
        testTable.put("b", "c");
        testTable.put("sasha", "dasha");
        testTable.clear();
        assertFalse(testTable.contains("testTable"));
        assertFalse(testTable.contains("b"));
    }

    @Test
    public void get() { //same as Contains, so just simple test.
        testTable.put("testTable", "testTable");
        testTable.put("testTable", "b");
        assertEquals("b", testTable.get("testTable"));
        assertNull(testTable.get("b"));
        testTable.remove("testTable");
        assertNull(testTable.get("testTable"));

        testTable.put("testTable", "b");
        testTable.clear();
        assertNull(testTable.get("testTable"));
    }

    @Test
    public void getAfterExpanding() { //same as Contains, so just simple test.
        String s = "testTable";
        for (int i = 0; i < 32; i++) {
            s = "testTable";
            testTable.put(s, s + "b");
        }

        for (int i = 0; i < 32; i++){
            assertEquals(s + "b", testTable.get(s));
        }
    }

    @Test
    public void putSimple() {
        testTable.put("testTable", "b");
        assertTrue(testTable.contains("testTable"));
    }

    @Test
    public void putSameObject() {
        testTable.put("testTable", "b");
        testTable.put("testTable", "b");
        assertEquals("b", testTable.get("testTable"));
        assertEquals(1, testTable.size());
    }

    @Test
    public void putSameObjectAfterExpanding() {
        String s = "testTable";
        for (int i = 0; i < 32; i++) {
            s += "testTable";
            testTable.put(s, s + "b");
        }

        s = "testTable";
        for (int i = 0; i < 32; i++) {
            s += "testTable";
            testTable.put(s, s + "c");
            assertEquals(s + "c", testTable.get(s));
        }
    }

    @Test
    public void removeInEmptyTable() {
        assertNull(testTable.remove("testTable"));
        assertEquals(0, testTable.size());
    }

    @Test
    public void removeExistingObject() {
        testTable.put("testTable", "b");
        assertEquals("b", testTable.remove("testTable"));
        assertEquals(0, testTable.size());
    }

    @Test
    public void removeNotExistingObject() {
        testTable.put("testTable", "b");
        testTable.put("b", "c");
        assertNull(testTable.remove("c"));
        assertEquals(2, testTable.size());
    }

    @Test
    public void clearEmptyTable() {
        testTable.clear();
        assertEquals(0, testTable.size());
    }

    @Test
    public void clearBigTable() {
        testTable.put("testTable", "b");
        testTable.put("b", "c");
        testTable.put("c", "d");
        testTable.put("d", "e");
        testTable.clear();
        assertEquals(0, testTable.size());
    }

    @Test
    public void clearAfterExpanding() {
        String s = "testTable";
        for (int i = 0; i < 32; i++) {
            s += "testTable";
            testTable.put(s, s);
        }

        testTable.clear();
        assertEquals(0, testTable.size());
    }

    @Test
    public void PuttingNullShouldThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> testTable.put(null, "b"));
        assertThrows(IllegalArgumentException.class, () -> testTable.put("a", null));
        assertThrows(IllegalArgumentException.class, () -> testTable.put(null, null));
    }

    @Test
    public void GettingNullShouldThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> testTable.get(null));
        testTable.put("5", "6");
        assertThrows(IllegalArgumentException.class, () -> testTable.get(null));
    }

    @Test
    public void ContainsNullShouldThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> testTable.contains(null));
        testTable.put("5", "6");
        assertThrows(IllegalArgumentException.class, () -> testTable.contains(null));
    }

    @Test
    public void RemoveNullShouldThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> testTable.remove(null));
        testTable.put("5", "6");
        assertThrows(IllegalArgumentException.class, () -> testTable.remove(null));
    }
}