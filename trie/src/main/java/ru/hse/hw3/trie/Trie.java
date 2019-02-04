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
        root = new Node(true);
    }

    /**
     * Tries to find the longest prefix of element that exists in trie (not necessary as terminal string)
     *
     * If changeSize is 0, does not change sizes of nodes, changeSize is 1, increase it by 1 (using to add new string to trie),
     * if changeSize if -1, decrease it by 1 (using to remove terminal string from trie after making sure this terminal string exists), in any other case throws exception
     *
     * Returns a pair (packed in special class) of found Node and corresponding element's prefix.
     */
    @NotNull
    private ParsedNode findDeepestExistingNode(@NotNull String element, int changeSize) {
        if (changeSize != -1 && changeSize != 0 && changeSize != 1) {
            throw new IllegalArgumentException("changeSize should be either -1, 0 or 1");
        }

        Node currentNode = root;
        for (int i = 0; i < element.length(); i++) {
            if (changeSize == 1) {
                currentNode.increaseSize();
            } else
            if (changeSize == -1) {
                currentNode.decreaseSize();
            }

            Node sonNode = currentNode.getSonNode(element.charAt(i));
            if (sonNode == null) {
                return new ParsedNode(currentNode, i);
            } else {
                currentNode = sonNode;

                if (changeSize == -1 && sonNode.getSize() == 1) {
                    /*
                    Son node represents only one terminal string and it's going to be deleted, so we erasing memory by deleting reference to it
                    Since we always deletes son nodes, root of the trie will never be deleted
                    */
                    currentNode.removeSonNode(element.charAt(i));
                }
            }
        }

        return new ParsedNode(currentNode, element.length()); //Trie contains entire given string (as prefix of terminal).
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
     * Adds terminal string to the trie
     * Returns true if Trie already contained given terminal string
     */
    public boolean add(@NotNull String element) {
        ParsedNode parsedNode = findDeepestExistingNode(element, 1); //I believe type of this variable is not obvious from the context so I'm not using var on purpose
        Node currentNode = parsedNode.getParsedNode();
        for (int i = parsedNode.getParsedPrefix(); i < element.length(); i++) {
            currentNode = currentNode.createSonNode(element.charAt(i));
        }

        currentNode.increaseTerminality();
        return currentNode.getTerminality() > 1;
    }

    /**
     * True if trie contains given terminal string
     */
    public boolean contains(@NotNull String element) {
        ParsedNode parsedNode = findDeepestExistingNode(element, 0);
        return parsedNode.getParsedPrefix() == element.length() &&
                parsedNode.getParsedNode().isTerminal();
    }

    /**
     * Removes given terminal string from trie.
     * Return false if there was no such terminal string in trie.
     */
    public boolean remove(@NotNull String element) {
        if (!contains(element)) {
            return false;
        }

        /*
        Since we checked trie contains given string as terminal, it surely contains corresponding Node, so we should just decrease sizes of Nodes
         */
        findDeepestExistingNode(element, -1);
        return true;
    }

    /**
     * Returns number of terminal strings in Trie
     */
    public int size() {
        return root.getSize();
    }

    /**
     * Returns number of terminal strings started with given prefix
     * Return zero if Trie does not contains given string as prefix of any terminal string
     */
    public int howManyStartsWithPrefix(@NotNull String prefix) {
        ParsedNode parsedNode = findDeepestExistingNode(prefix, 0);
        if (parsedNode.getParsedPrefix() != prefix.length()) {
            return 0;
        }

        return parsedNode.getParsedNode().getSize();
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
            size = 1; //All nodes except for root should always be creating and existing to represent some stored string (size > 0)
            terminalSize = 0;
        }

        /**
         * Root remains the only exception where size of the Node could be zero. This constructor supposed to be only for root Node
         */
        private Node(boolean isRoot) {
            this();
            if (isRoot) {
                size = 0;
            }
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
        private Node createSonNode(char key) {
            /*
            Should I throw IllegalArgumentExceptions etc in the non-public interface or I may avoid it for optimisation purposes?
             */
            if (sonNodes.containsKey(key)) {
                throw new IllegalArgumentException("Can't create son node cause node with given key already exists");
            }

            var newNode = new Node();
            sonNodes.put(key, newNode);
            return newNode;
        }

        /**
         * Removes son node with given key from sons of this node. Throws exception if there is no son node with given key
         */
        private void removeSonNode(char key) {
            if (!sonNodes.containsKey(key)) {
                throw new IllegalArgumentException("Can't remove son node with given key cause it does not exist");
            }

            sonNodes.remove(key);
        }

        /**
         * Makes one more terminal String end in this Node, which means increasing terminalSize by 1.
         */
        private void increaseTerminality() {
            terminalSize++;
        }

        /**
         * Makes one less terminal String end in this Node, which means decreasing terminalSize by 1.
         */
        private void decreaseTerminality() {
            terminalSize--;
        }

        /**
         * Marks that one more terminal string ends in this subtree
         */
        private void increaseSize() {
            size++;
        }

        /**
         * Marks that string was deleted from this subtree
         */
        private void decreaseSize() {
            size--;
        }

        /**
         * Returns number of terminal string in this subtree
         */
        private int getSize() {
            return size;
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

        /**
         * Returns number of terminal strings ended in this Node
         */
        private int getTerminality() {
            return terminalSize;
        }
    }
}
