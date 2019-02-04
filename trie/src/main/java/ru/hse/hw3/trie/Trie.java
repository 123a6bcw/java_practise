package ru.hse.hw3.trie;

import java.util.HashMap;

public class Trie {

    /**
     * Class representing the node inside trie.
     */
    private class Node {
        /**
         * By char gives Node representing corresponding child string for this node.
         */
        private HashMap<Character, Node> sonNodes;

        private Node() {
            sonNodes = new HashMap<>();
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
        private Node createSonNode(char key) {
            /*
            Should I throw IllegalArgumentExceptions etc in the non-public interface or I may avoid it for optimisation purposes?
             */
            if (sonNodes.containsKey(key)) {
                throw new IllegalArgumentException("Can't create son node cause node with given key already exists");
            }

            return sonNodes.put(key, new Node());
        }
    }
}
