package ru.hse.hw3.unbalancedtreeset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;

public class UnbalancedTreeSet<E> extends AbstractSet<E> implements MyTreeSet<E> {
    private Node<E> root = null;
    private int version = 0;
    private Comparator<? super E> comparator;
    private int size = 0;

    UnbalancedTreeSet(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    /**
     * If set does not contain element with the same value, adds given element to the set and returns true
     * Otherwise does nothing and returns false
     */
    @Override
    public boolean add(E e) {
        if (root == null) {
            root = new Node<E>(e, null);
        } else
        if (!add(e, root)) {
            return false;
        }

        size++;
        upgradeVersion();
        return true;
    }

    /**
     * Adds value to the node's subtree in the correct order
     * Returns false if element with the same value already exists in node's subtree
     */
    private boolean add(E e, @NotNull Node<E> node) {
        int compareResult = comparator.compare(e, node.getValue());
        if (compareResult == 0) {
            return false;
        }

        Vector vector;
        if (compareResult > 0) {
            vector = Vector.RIGHT;
        } else {
            vector = Vector.LEFT;
        }

        if (node.hasSon(vector)) {
            return add(e, node.sonAtVector(vector));
        } else {
            node.setSon(new Node<>(e, node), vector);
            return true;
        }
    }

    /**
     * Increases current tree's version after modifications of the tree.
     * May overflow, but that's ok.
     */
    private void upgradeVersion() {
        version++;
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new UnbalancedTreeSetIterator(version, Vector.RIGHT);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new UnbalancedTreeSetIterator(version, Vector.LEFT);
    }

    @Override
    public MyTreeSet<E> descendingSet() {
        return null;
    }

    @Override
    public E first() {
        return null;
    }

    @Override
    public E last() {
        return null;
    }

    @Override
    public E lower(E e) {
        return null;
    }

    @Override
    public E floor(E e) {
        return null;
    }

    @Override
    public E ceiling(E e) {
        return null;
    }

    @Override
    public E higher(E e) {
        return null;
    }

    /**
     *
     */
    private enum Vector {
        LEFT, RIGHT;

        private Vector opposite() {
            if (this == LEFT) {
                return RIGHT;
            } else {
                return LEFT;
            }
        }
    };

    private int getTreeVersion() {
        return version;
    }

    /**
     * Class for iterating over tree in ascending order
     */
    private class UnbalancedTreeSetIterator implements Iterator<E> {
        /**
         * Current Node iterator is point to
         */
        Node<E> current;

        /**
         * If vector is LEFT, rightmost Node is the starting Node, next() moves position to the LEFT direction
         * vice versa for LEFT vector
         * Thus, RIGHT vector build ascending iterator, LEFT build descending
         */
        @NotNull
        private Vector vector;

        /**
         * Version of Tree when iterator has been created. If differs from actual Tree's version, iterator is invalid
         */
        private int version;

        private UnbalancedTreeSetIterator(int version, @NotNull Vector vector) {
            this.version = version;
            this.vector = vector;
            current = root.getDeepest(vector.opposite());
        }

        /**
         * Throws exception if version of this iterator differs from version of Tree, meaning there was modification
         * after creation of this iterator
         */
        private void checkVersion() {
            if (version != getTreeVersion()) {
                throw new ConcurrentModificationException("Iterator is invalid after tree modification");
            }
        }

        @Override
        public boolean hasNext() {
            checkVersion();

            return getNextNode(current) != null;
        }

        @Override
        @NotNull
        public E next() {
            checkVersion();

            current = getNextNode(current);
            if (current == null) {
                throw new NoSuchElementException("No next element in tree");
            }
            return current.getValue();
        }

        /**
         * Returns the next Node in order
         * Returns null if there is no such
         */
        @Nullable
        private Node<E> getNextNode(@NotNull Node<E> node) {
            if (node.getParent() == null) {
                return null;
            }

            if (node.hasSon(vector)) {
                return node.sonAtVector(vector).getDeepest(vector.opposite());
            }

            if (node.isVectorSon(vector.opposite())) {
                return node.getParent();
            }

            return getNextNode(node.getParent());
        }
    }

    /**
     * Class representing one node inside tree.
     */
    private static class Node<E> {
        /**
         * Value stored in Node
         */
        @NotNull
        private E value;

        /**
         * Left son Node in tree
         */
        @SuppressWarnings("unchecked")
        private Node<E>[] sons = (Node<E>[]) new Node[2];

        /**
         * Parent Node in tree
         */
        @Nullable
        private Node<E> parent;

        private Node(@NotNull E value, @Nullable Node<E> parent) {
            this.value = value;
            this.parent = parent;
        }

        @NotNull
        private E getValue() {
            return value;
        }

        /**
         * Returns leftmost or rightmost (depending on vector value) Node in Node's subtree
         */
        @NotNull
        private Node<E> getDeepest(@NotNull Vector vector) {
            if (hasSon(vector)) {
                return sonAtVector(vector).getDeepest(vector);
            }

            return this;
        }

        /**
         * Returns true if direction of moving from parent of this node to this node equals to vector
         */
        private boolean isVectorSon(@NotNull Vector vector) {
            if (parent == null) {
                return false;
            }
            return parent.getVectorSon(vector) == this;
        }

        /**
         *
         */
        @Nullable
        private Node<E> getVectorSon(@NotNull Vector vector) {
            return sons[vector.ordinal()];
        }

        /**
         * Returns if Node has son in direction of vector
         */
        private boolean hasSon(@NotNull Vector vector) {
            return getVectorSon(vector) != null;
        }
        /**
         * Get son in direction of vector
         */
        @NotNull
        private Node<E> sonAtVector(@NotNull Vector vector) {
            if (!hasSon(vector)) {
                throw new IllegalArgumentException("Node does not have " + vector.name() + " son");
            }

            //on the if above we checked return statement won't be null, but IDEA can't process it correctly
            return Objects.requireNonNull(getVectorSon(vector));
        }

        private Node<E> getParent() {
            return parent;
        }

        /**
         * Set son of direction of vector
         */
        private void setSon(Node<E> leftSon, @NotNull Vector vector) {
            sons[vector.ordinal()] = leftSon;
        }
    }
}