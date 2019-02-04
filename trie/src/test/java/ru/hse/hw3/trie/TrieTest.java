package ru.hse.hw3.trie;

import org.junit.jupiter.api.BeforeEach;
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
    void add() {
    }

    @Test
    void contains() {
    }

    @Test
    void remove() {
    }

    @Test
    void howManyStartsWithPrefix() {
    }
}