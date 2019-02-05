package ru.hse.hw3.unbalancedtreeset;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class UnbalancedTreeSet<E> extends AbstractSet<E> implements MyTreeSet<E> {
    Node root = null;
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
     * Class representing one node inside tree.
     */
    private static class Node<E> {
        /**
         *
         */
        enum Vector {LEFT, RIGHT};

        /**
         * Value stored in Node
         */
        private E value;

        /**
         * Left son Node in tree
         */
        @SuppressWarnings("unchecked")
        private Node<E>[] sons = (Node<E>[]) new Node[2];

        /**
         * Parent Node in tree
         */
        private Node<E> parent;

        private Node(E value, Node<E> parent) {
            this.value = value;
            this.parent = parent;
        }

        private E getValue() {
            return value;
        }

        /**
         * Returns leftmost or rightmost (depending on vector value) Node in Node's subtree
         */
        @Nullable
        private Node<E> getDeepest(Vector vector) {
            if (getSon(vector) != null) {
                return getSon(vector).getDeepest(vector);
            }

            return this;
        }

        /**
         * Returns true if direction of moving from parent of this node to this node equals to vector
         */
        private boolean isVectorSon(Vector vector) {
            return parent.getSon(vector) == this;
        }

        /**
         * Get son in direction of vector
         */
        @Nullable
        private Node<E> getSon(Vector vector) {
            return sons[vector.ordinal()];
        }

        private Node<E> getParent() {
            return parent;
        }

        /**
         * Set son of direction of vector
         */
        private void setSon(Node<E> leftSon, Vector vector) {
            sons[vector.ordinal()] = leftSon;
        }

        /**
         * Returns if Node has son in direction of vector
         */
        private boolean hasSon(Vector vector) {
            return getSon(vector) != null;
        }
    }
}