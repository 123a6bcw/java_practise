package ru.hse.hw3.unbalancedtreeset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;

public class UnbalancedTreeSet<E> extends AbstractSet<E> implements MyTreeSet<E> {
    Node<E> root = null;
    private int version = 0;
    private Comparator<? super E> comparator;

    @Override
    public Iterator<E> iterator() {
        return new UnbalancedTreeSetIterator<E>(version);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return null;
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
    private enum Vector {LEFT, RIGHT};

    /**
     *
     */
    private static Vector oppositeDirection(Vector vector) {
        if (vector == Vector.LEFT) {
            return Vector.RIGHT;
        } else {
            return Vector.LEFT;
        }
    }

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
         * If vector is LEFT, leftmost Node is the starting Node, next() moves position to the RIGHT direction
         * vice versa for RIGHT vector
         * Thus, LEFT vector build ascending iterator, RIGHT build descending
         */
        @NotNull
        private Vector vector;

        /**
         * Version of Tree when iterator has been created. If differs from actual version, iterator is invalid
         */
        private int version;

        private UnbalancedTreeSetIterator(int version, @NotNull Vector vector) {
            this.version = version;
            this.vector = vector;
            current = root.getDeepest(oppositeDirection(vector));
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
                //requireNonNull is redundant here, but IDEA can't process it for some reason
                return Objects.requireNonNull(node.getSon(vector)).getDeepest(oppositeDirection(vector));
            }

            if (node.isVectorSon(oppositeDirection(vector))) {
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
                //requireNonNull quite redundant here, but IDEA can't process it :(
                return Objects.requireNonNull(getSon(vector)).getDeepest(vector);
            }

            return this;
        }

        /**
         * Returns true if direction of moving from parent of this node to this node equals to vector
         */
        private boolean isVectorSon(@NotNull Vector vector) {
            return parent.getSon(vector) == this;
        }

        /**
         * Get son in direction of vector
         */
        @Nullable
        private Node<E> getSon(@NotNull Vector vector) {
            return sons[vector.ordinal()];
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

        /**
         * Returns if Node has son in direction of vector
         */
        private boolean hasSon(@NotNull Vector vector) {
            return getSon(vector) != null;
        }
    }
}