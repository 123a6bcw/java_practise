package ru.hse.hw3.unbalancedtreeset;

import java.util.AbstractSet;
import java.util.Iterator;

public class UnbalancedTreeSet<E> extends AbstractSet<E> implements MyTreeSet<E> {
    Node root;

    @Override
    public Iterator<E> iterator() {
        return null;
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
     * Not static in order to correspond version of the tree
     */
    private class Node<E> {
        /**
         * Value stored in Node
         */
        private E value;

        /**
         * Left son Node in tree
         */
        private Node leftSon;

        /**
         * Right son Node in tree
         */
        private Node rightSon;

        private Node(E value) {
            this.value = value;
            leftSon = null;
            rightSon = null;
        }

        private E getValue() {
            return value;
        }

        private Node getLeftSon() {
            return leftSon;
        }

        private Node getRightSon() {
            return rightSon;
        }

        private void setLeftSon(Node leftSon) {
            this.leftSon = leftSon;
        }

        private void setRightSon(Node rightSon) {
            this.rightSon = rightSon;
        }
    }
}