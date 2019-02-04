package ru.hse.hw3.trie;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * Tries to find the longest prefix of element that exists in trie (not necessary as terminal string)
     * Returns a pair (packed in special class) of found Node and corresponding element's prefix
     */
    @NotNull
    private ParsedNode findDeepestExistingNode(@NotNull String element) {
        Node currentNode = root;
        for (int i = 0; i < element.length(); i++) {
            Node sonNode = currentNode.getSonNode(element.charAt(i));
            if (sonNode == null) {
                return new ParsedNode(currentNode, i);
            } else {
                currentNode = sonNode;
            }
        }

        return new ParsedNode(currentNode, element.length());
    }

    /**
     * Class for storing pair of found Node and prefix of element using in findDeepestExistingNode method
     */
    private class ParsedNode {
        private Node parsedNode;
        private int parsedPrefix;

        private ParsedNode(@NotNull Node parsedNode, int parsedPrefix) {
            this.parsedNode = parsedNode;
            this.parsedPrefix = parsedPrefix;
        }

        @NotNull
        private Node getParsedNode() {
            return parsedNode;
        }

        private int getParsedPrefix() {
            return parsedPrefix;
        }
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
        @Nullable
        private Node getSonNode(char key) {
            return sonNodes.get(key);
        }

        /**
         * Creates new empty Node with given key, throws exception if Node with given key already exists.
         */
        @NotNull
        private Node createSonNode(char key, boolean isTerminal) {
            /*
            Should I throw IllegalArgumentExceptions etc in the non-public interface or I may avoid it for optimisation purposes?
             */
            if (sonNodes.containsKey(key)) {
                throw new IllegalArgumentException("Can't create son node cause node with given key already exists");
            }

            //noinspection ConstantConditions --- there is no need since we checking not nullability in the above if statement.
            return sonNodes.put(key, new Node());
        }

        /**
         * Make one more terminal String end in this Node, which means increasing size and terminalSize by 1.
         */
        private void increaseTerminality() {
            size++;
            terminalSize++;
        }

        /**
         * Returns true if there is no terminal strings in given subtree
         */
        private boolean isEmpty() {
            return size == 0;
        }

        /**
         * Return true if there is terminal string ended in this Node.
         */
        private  boolean isTerminal() {
            return terminalSize > 0;
        }
    }
}
