package ru.hse.hw3.unbalancedtreeset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Elements of type E set collection based on Heap data structure
 * All values in the right subtree of the node is always strictly greater than value in the node, all values in the left
 * subtree is always strictly less.
 */
public class UnbalancedTreeSet<E> extends AbstractSet<E> implements MyTreeSet<E> {
    /**
     *
     */
    private static class TreeState<E> {
        /**
         * Current Tree's version. Changes after modifications.
         */
        private int version = 0;

        /**
         * Number of elements in set.
         */
        private int size = 0;

        /**
         * Root of the tree
         */
        private Node<E> root;

        /**
         * Comparator of elements inside the set.
         */
        private Comparator<? super E> comparator;
    }

    /**
     *
     */
    private TreeState<E> treeState;

    /**
     *
     */
    protected int getSize() {
        return treeState.size;
    }

    /**
     *
     */
    protected int getTreeVersion() {
        return treeState.version;
    }

    protected void increaseSize() {
        treeState.size++;
    }

    /**
     * Increases current tree's version after modifications of the tree.
     * May overflow, but that's ok.
     */
    protected void upgradeVersion() {
        treeState.version++;
    }

    /**
     *
     */
    protected Comparator<? super E> getComparator() {
        return treeState.comparator;
    }

    /**
     *
     */
    protected void setComparator(Comparator<? super E> comparator) {
        treeState.comparator = comparator;
    }

    /**
     *
     */
    protected Node<E> getRoot() {
        return treeState.root;
    }

    /**
     *
     */
    protected void setRoot(Node<E> root) {
        treeState.root = root;
    }

    protected enum Vector {
        LEFT, RIGHT;

        private Vector opposite() {
            if (this == LEFT) {
                return Vector.RIGHT;
            } else {
                return Vector.LEFT;
            }
        }
    }

    protected Vector getLeftVector() {
        return Vector.LEFT;
    }

    protected Vector getRightVector() {
        return Vector.RIGHT;
    }


    public UnbalancedTreeSet(Comparator<? super E> comparator) {
        treeState = new TreeState<>();
        setRoot(null);
        setComparator(comparator);
    }

    public UnbalancedTreeSet() {
        treeState = new TreeState<>();
        setRoot(null);
        setComparator(null);
    }

    /**
     * Compare objects using given comparator or by asuming objects in set are comparable.
     * If both comparator not specified and objects aren't comparable, Set fails.
     */
    @SuppressWarnings("unchecked")
    private int compare(Object o1, Object o2) {
        if (getComparator() == null) {
            return ((Comparable<? super E>)o1).compareTo((E) o2);
        } else {
            return getComparator().compare((E) o1, (E)o2);
        }
    }

    /**
     * If set does not contain element with the same value, adds given element to the set and returns true
     * Otherwise does nothing and returns false
     */
    @Override
    public boolean add(E e) {
        if (getRoot() == null) {
            setRoot(new Node<E>(e, null));
        } else
        if (!add(e, getRoot())) {
            return false;
        }

        increaseSize();
        upgradeVersion();
        return true;
    }

    /**
     * Adds value to the node's subtree in the correct order
     * Returns false if element with the same value already exists in node's subtree
     */
    private boolean add(E e, @NotNull Node<E> node) {
        int compareResult = compare(e, node.getValue());
        if (compareResult == 0) {
            return false;
        }

        Vector vector;
        if (compareResult > 0) {
            vector = getRightVector();
        } else {
            vector = getLeftVector();
        }

        if (node.hasSon(vector)) {
            return add(e, node.sonAtVector(vector));
        } else {
            node.setSon(new Node<>(e, node), vector);
            return true;
        }
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new UnbalancedTreeSetIterator(getTreeVersion(), getRightVector());
    }

    @Override
    public int size() {
        return getSize();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new UnbalancedTreeSetIterator(getTreeVersion(), getLeftVector());
    }

    @Override
    public MyTreeSet<E> descendingSet() {
        return new DescendingTreeSet<>(this);
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
            current = getRoot().getDeepest(vector.opposite());
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

        @Nullable
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

    private static class DescendingTreeSet<E> extends UnbalancedTreeSet<E> {
        UnbalancedTreeSet<E> superSet;

        DescendingTreeSet(UnbalancedTreeSet<E> ascendingSet) {
            superSet = ascendingSet;
        }

        @Override
        public int size() {
            return superSet.size();
        }

        /**
         *
         */
        @Override
        protected Comparator<? super E> getComparator() {
            return superSet.getComparator();
        }

        /**
         *
         */
        @Override
        protected void setComparator(Comparator<? super E> comparator) {
            superSet.setComparator(comparator);
        }

        /**
         *
         */
        protected Node<E> getRoot() {
            return superSet.getRoot();
        }

        /**
         *
         */
        protected void setRoot(Node<E> root) {
            superSet.setRoot(root);
        }

        protected int getSize() {
            return superSet.getSize();
        }

        /**
         *
         */
        protected int getTreeVersion() {
            return superSet.getTreeVersion();
        }

        protected void increaseSize() {
            superSet.increaseSize();
        }

        /**
         * Increases current tree's version after modifications of the tree.
         * May overflow, but that's ok.
         */
        protected void upgradeVersion() {
            superSet.upgradeVersion();
        }

        /**
         *
         * @return
         */
        protected Vector getLeftVector() {
            return Vector.RIGHT;
        }

        /**
         *
         * @return
         */

        protected Vector getRightVector() {
            return Vector.LEFT;
        }
    }
}