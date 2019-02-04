package ru.hse.hw3.trie;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {
    private Trie trie = new Trie();

    @Test
    void sizeOfEmptyTrieReturnZero() {
        assertEquals(0, trie.size());
    }

    @Test
    void sizeOfTrieWithOneElementReturnsOne() {
        trie.add("test2");
        assertEquals(1, trie.size());
    }

    @Test
    void sizeOfTrieAfterRemovingAllElementsReturnZero() {
        trie.add("a");
        trie.add("b");
        trie.add("c");
        trie.remove("a");
        trie.remove("b");
        trie.remove("c");
        assertEquals(0, trie.size());
    }

    @Test
    void sizeOfDuplicatedStringCountsCorrectly() {
        trie.add("a");
        trie.add("a");
        trie.add("a");
        assertEquals(3, trie.size());
        trie.remove("a");
        trie.remove("a");
        assertEquals(1, trie.size());
        trie.remove("a");
        assertEquals(0, trie.size());
    }

    @Test
    void sizeAfterRemovingNotExistingObjectDoesNotChange() {
        trie.add("a");
        trie.add("b");
        trie.remove("c");
        assertEquals(2, trie.size());
    }

    @Test
    void sizeAfterAddingComplexStringCountsCorrectly() {
        trie.add("//--longString !!! with Stran\nge &&&&***!!! charact____ers____\n");
        assertEquals(1, trie.size());
        trie.remove("//--longString !!! with Stran\nge &&&&***!!! charact____ers____\n");
        assertEquals(0, trie.size());
    }

    @Test
    void sizeOfComplexTrieCountsCorrectly() {
        trie.add("a");
        assertEquals(1, trie.size());
        trie.add("b");
        trie.add("a");
        trie.add("e");
        assertEquals(4, trie.size());
        trie.remove("f");
        trie.remove("e");
        assertEquals(3, trie.size());
        trie.add("abc");
        assertEquals(4, trie.size());
        trie.remove("abc");
        trie.remove("a");
        trie.remove("b");
        trie.remove("a");

        assertEquals(0, trie.size());
    }

    @Test
    void sizeOfEmptyStringWorksCorrectly() {
        trie.add("");
        assertEquals(1, trie.size());
        trie.add("");
        assertEquals(2, trie.size());
        trie.remove("");
        assertEquals(1, trie.size());
        trie.remove("");
        assertEquals(0, trie.size());
    }

    /*
    Size functionality does not tested since it's already been tested
     */
    @Test
    void addOneElementWorksCorrectly() {
        trie.add("abc");
        assertTrue(trie.contains("abc"));
    }

    @Test
    void addsDuplicatedElementsWorksCorrectly() {
        trie.add("abc");
        trie.add("abc");
        trie.add("abc");
        trie.add("bce");
        trie.add("zuezue");
        assertTrue(trie.contains("abc"));
        assertTrue(trie.contains("bce"));
        assertTrue(trie.contains("zuezue"));
    }

    @Test
    void addsEmptyStringWorksCorrectly() {
        trie.add("");
        assertTrue(trie.contains(""));
    }

    @Test
    void addsExistingElementReturnFalse() {
        trie.add("abc");
        assertFalse(trie.add("abc"));
        trie.add("");
        assertFalse(trie.add(""));
    }

    @Test
    void addsNewElementsReturnsTrue() {
        assertTrue(trie.add("abc"));
        assertTrue(trie.add("bc"));
        assertTrue(trie.add("a"));
        assertTrue(trie.add(""));
        assertTrue(trie.add("abcd"));
    }


    @Test
    void containsInEmptyTrieReturnsFalse() {
        assertFalse(trie.contains("a"));
        assertFalse(trie.contains("bce"));
    }

    @Test
    void containsNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> trie.contains(null));
    }

    @Test
    void containsNotAddedEmptyStringReturnsFalse() {
        trie.add("a");
        assertFalse(trie.contains(""));
    }

    @Test
    void containsRemovesElementReturnsFalse() {
        trie.add("abc");
        assertTrue(trie.contains("abc"));
        trie.remove("abc");
        assertFalse(trie.contains("abc"));
    }

    @Test
    void containsAddedEmptyStringReturnTrue() {
        trie.add("");
        assertTrue(trie.contains(""));
        trie.remove("");
        assertFalse(trie.contains(""));
    }

    @Test
    void containsAfterRemovesAnotherObjectStaysTheSame() {
        trie.add("abc");
        trie.add("abd");
        trie.remove("bce");
        assertTrue(trie.contains("abc"));
    }

    @Test
    void containsOfDuplicatedElementsWorksCorrectly() {
        trie.add("abc");
        trie.add("abc");
        trie.remove("abc");
        assertTrue(trie.contains("abc"));
        trie.remove("abc");
        assertFalse(trie.contains("abc"));
    }


    /*
    Main functionality was tested above, so just cimple tests
     */
    @Test
    void removeInEmptyTrieDoesNotThrow() {
        assertDoesNotThrow(() -> trie.remove("abc"));
    }

    @Test
    void removeExistedElementTwiceDoesNotThrow() {
        trie.add("abc");
        assertDoesNotThrow(() -> trie.remove("abc"));
        assertDoesNotThrow(() -> trie.remove("abc"));
    }

    @Test
    void howManyStartsWithPrefixInEmptyTrieReturnZero() {
        assertEquals(0, trie.howManyStartsWithPrefix(""));
    }

    @Test
    void howManyStartsWithPrefixOfEmptyStringReturnsSize() {
        trie.add("5");
        assertEquals(trie.size(), trie.howManyStartsWithPrefix(""));
        trie.add("abc");
        assertEquals(trie.size(), trie.howManyStartsWithPrefix(""));
        trie.add("abc");
        assertEquals(trie.size(), trie.howManyStartsWithPrefix(""));
        trie.add("cde");
        trie.remove("abc");
        assertEquals(trie.size(), trie.howManyStartsWithPrefix(""));
        trie.remove("5");
        trie.remove("cde");
        assertEquals(trie.size(), trie.howManyStartsWithPrefix(""));
    }

    @Test
    void howManyStartsWithPrefixWithOneStringInTrieReturnOneOrZero() {
        trie.add("abc");
        assertEquals(1, trie.howManyStartsWithPrefix("a"));
        assertEquals(1, trie.howManyStartsWithPrefix("ab"));
        assertEquals(1, trie.howManyStartsWithPrefix("abc"));
        assertEquals(0, trie.howManyStartsWithPrefix("abcd"));
        assertEquals(0, trie.howManyStartsWithPrefix("b"));
    }

    @Test
    void howManyStartsWithPrefixWithOneDuplicatedString() {
        trie.add("abc");
        trie.add("abc");
        assertEquals(2, trie.howManyStartsWithPrefix("a"));
        assertEquals(2, trie.howManyStartsWithPrefix("ab"));
        assertEquals(2, trie.howManyStartsWithPrefix("abc"));
        assertEquals(0, trie.howManyStartsWithPrefix("abcd"));
        assertEquals(0, trie.howManyStartsWithPrefix("b"));
    }

    @Test
    void howManyStartsWithPrefixInBranchingTrie() {
        trie.add("abc");
        trie.add("abc");
        trie.add("bcd");
        trie.add("bbb");
        trie.add("a");
        trie.add("cde");
        trie.add("bde");

        assertEquals(3, trie.howManyStartsWithPrefix("a"));
        assertEquals(3, trie.howManyStartsWithPrefix("b"));
        assertEquals(1, trie.howManyStartsWithPrefix("c"));
        assertEquals(2, trie.howManyStartsWithPrefix("ab"));
        assertEquals(2, trie.howManyStartsWithPrefix("abc"));
        assertEquals(1, trie.howManyStartsWithPrefix("bb"));
        assertEquals(1, trie.howManyStartsWithPrefix("bc"));
        assertEquals(1, trie.howManyStartsWithPrefix("bbb"));
        assertEquals(0, trie.howManyStartsWithPrefix("ca"));
        assertEquals(0, trie.howManyStartsWithPrefix("d"));
    }

}