package ru.hse.hw3.trie;

import java.util.HashMap;

/**
 * Class-collection of Strings using the trie algorithm.
 *
 * Abstractly, one may see this class as an expanding array of strings. Let's call strings added by method "add(String element)"
 * as "terminal strings". Method "add" will strictly increase number of terminal strings by 1 (which allows one to store duplicated strings),
 * and method "remove(String element)" can only remove terminal string if this string stored in trie as a terminal string.
 *
 * As one can expect that from trie, limited work with the prefixes of terminal strings is supported, too.
 */
public class Trie {
    /**
     * Root of the trie
     */
    private Node root;

    public Trie() {
        root = new Node();
    }



    /**
     * Class representing the node inside trie.
     */
    private class Node {
        /**
         * By char gives Node representing corresponding child string for this node.
         */
        private HashMap<Character, Node> sonNodes;

        /**
         * How many terminal strings ended somewhere in subtree of this Node
         */
        private int size;

        /**
         * How many terminal strings ended exactly in this Node
         */
        private int terminalSize;

        private Node() {
            sonNodes = new HashMap<>();
            size = 0; //Still prefer to do it manually (just in case)
            terminalSize = 0;
        }

        /**
         * By char gives Node representing corresponding child string for this node.
         */
        private Node getSonNode(char key) {
            return sonNodes.get(key);
        }

        /**
         * Creates new empty Node with given key, throws exception if Node with given key already exists.
         */
        private Node createSonNode(char key, boolean isTerminal) {
            /*
            Should I throw IllegalArgumentExceptions etc in the non-public interface or I may avoid it for optimisation purposes?
             */
            if (sonNodes.containsKey(key)) {
                throw new IllegalArgumentException("Can't create son node cause node with given key already exists");
            }

            return sonNodes.put(key, new Node());
        }

        /**
         * Make one more terminal String end in this Node, which means increasing size and terminalSize by 1.
         */
        private void increaseTerminality() {
            size++;
            terminalSize++;
        }
    }
}
