package ru.hse.hw3.unbalancedtreeset;

import com.sun.source.tree.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreeCellRenderer;
import java.util.*;

/**
 * Elements of type E set collection based on Heap data structure
 * All values in the right subtree of the node is always strictly greater than value in the node, all values in the left
 * subtree is always strictly less.
 */
public class UnbalancedTreeSet<E> extends AbstractCollection<E> implements MyTreeSet<E> {
    /**
     * Class for storing tree's state (basically all mutable fields). See DescendingTreeSet to see details of necessity in using this class
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

        /**
         * Enum for moving
         * <p></p>
         * Placed here so programmer would not think about using TreeState.Vector.RIGHT/LEFT
         * to create instances of Vector (too bad Java can't forbid that) as it may cause errors in synchronization
         * with descendingTreeSet.
         */
        private enum Vector {
            LEFT, RIGHT;

            private Vector opposite() {
                if (this == LEFT) {
                    return Vector.RIGHT;
                } else {
                    return Vector.LEFT;
                }
            }
        }
    }

    /**
     * Current tree state. Heir classes does not have access to it and can't set it therefore cannot can't change
     * tree's state. See details in class DescendingTreeSet
     */
    private TreeState<E> treeState;

    /**
     * Creates new tree set, all elements will be sorted using custom comparator
     */
    public UnbalancedTreeSet(Comparator<? super E> comparator) {
        treeState = new TreeState<>();
        setRoot(null);
        setComparator(comparator);
    }

    /**
     * Creates new tree set, if element's type implements comparable interface, element will be sorted in corresponding
     * order. In other case, would cause ClassCastException
     */
    public UnbalancedTreeSet() {
        treeState = new TreeState<>();
        setRoot(null);
        setComparator(null);
    }

    /**
     * Compare objects using given comparator or by asuming objects in set are comparable.
     * If both comparator not specified and objects aren't comparable, throws ClassCastException.
     */
    /*
    Causes UncheckedCastWarning because cast may be wrong if user gave set elements with no comparable interface
    and didn't specify comparator
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

        var vector = getVectorByCompareResult(compareResult);

        if (node.hasSon(vector)) {
            return add(e, node.sonAtVector(vector));
        } else {
            node.setSon(new Node<>(e, node), vector);
            return true;
        }
    }

    /**
     * Creates iterator for iterating over elements in set in natural order.
     */
    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new UnbalancedTreeSetIterator(getTreeVersion(), getRightVector());
    }

    /**
     * Creates iterator for iterating over elements in set in reversed order
     */
    @Override
    public Iterator<E> descendingIterator() {
        return new UnbalancedTreeSetIterator(getTreeVersion(), getLeftVector());
    }

    /**
     * Returns size of the tree. Works in O(1) as tree maintains it's size
     */
    @Override
    public int size() {
        return getSize();
    }

    /**
     * Creates the same tree set but in reversed order. Changes of this object reflects reversed tree and vice versa
     * Invalidating iterators in one tree will also reflect iterators in another tree.
     */
    @Override
    public UnbalancedTreeSet<E> descendingSet() {
        return new DescendingTreeSet<>(this);
    }

    /**
     * Returns first element in set.
     * Throws NoSuchElementException if there is no elements in Set.
     */
    @Override
    public E first() {
        return iterator().next();
    }

    /**
     * Returns last element in set.
     * Throws NoSuchElementException if there is no elements in Set.
     */
    @Override
    public E last() {
        return descendingIterator().next();
    }

    /**
     * Finds greatest element lower than given one, or null if there is no such one.
     */
    @Override
    public E lower(E e) {
        return findVectoredValue(e, getLeftVector());
    }

    /**
     * Finds greatest element lower or equal to given one, or null if there is no such one
     */
    @Override
    public E floor(E e) {
        return findVectoredOrExactValue(e, getLeftVector());
    }

    /**
     * Find lowerst element greater or equal to given one, or null if there is no such one
     */
    @Override
    public E ceiling(E e) {
        return findVectoredOrExactValue(e, getRightVector());
    }

    /**
     * Find lowest element greater than given one, or null if there is no such one
     */
    @Override
    public E higher(E e) {
        return findVectoredValue(e, getRightVector());
    }

    /**
     * findVectoredValue started from the root, other parameters is the same.
     */
    /*
    Here and below I could copypast JavaDoc from findVectoredNode, but that would be terrible in size.
    Am I supposed to do that anyway?
     */
    private E findVectoredValue(Object value, TreeState.Vector vector) {
        return findVectoredValue(getRoot(), value, vector);
    }

    /**
     * findVectoredOrExactValue started from the root, other parameters is the same.
     */
    private E findVectoredOrExactValue(Object value, TreeState.Vector vector) {
        return findVectoredOrExactValue(getRoot(), value, vector);
    }

    /**
     * findVectoredOrExactNodeWithQualification starting from the root, other parameters is the same.
     */
    private AbstractMap.SimpleEntry<Node<E>, Boolean> findVectoredOrExactNodeWithQualification
    (Object value, TreeState.Vector vector) {
        return findVectoredOrExactNodeWithQualification(getRoot(), value, vector);
    }

    /**
     * Returns value of findVectoredValue with same parameters, or null if result is null
     */
    private E findVectoredValue(Node<E> node, Object value, TreeState.Vector vector) {
        Node<E> nodeResult = findVectoredNode(node, value, vector);
        if (nodeResult == null) {
            return null;
        }
        return nodeResult.getValue();
    }

    /**
     * Returns value of node found in findVectoredOrExactValueWithQualification with the same parameters, or
     * null if that node is null.
     */
    private E findVectoredOrExactValue(Node<E> node, Object value, TreeState.Vector vector) {
        var nodeResult = findVectoredOrExactNodeWithQualification(node, value, vector).getKey();
        if (nodeResult == null) {
            return null;
        }
        return nodeResult.getValue();
    }

    /**
     * RIGHT vector correspond to finding greater element (aka moving to the right son), LEFT vice versa
     * <p></p>
     * Finds Node with lowest value that strictly greater (greatest lower) than given value in given node's subtree
     * <p></p>
     * Returns null if there is no such Node
     */
    private Node<E> findVectoredNode(Node<E> node, Object value, TreeState.Vector vector) {
        AbstractMap.SimpleEntry<Node<E>, Boolean> qualifiedResult = findVectoredOrExactNodeWithQualification(node, value, vector);
        if (!qualifiedResult.getValue()) {
            return qualifiedResult.getKey();
        }
        return qualifiedResult.getKey().getNextNode(vector);
    }

    /**
     * RIGHT vector correspond to finding greater element (aka moving to the right son), LEFT vice versa
     * <p></p>
     * First element in pair is Node with lowest value that strictly greater (greatest lower) than given value in given node's subtree,
     * null if there is no such Node
     * <p></p>
     * Second element in pair is true if value in found node is equal to given one, false if found node is null or have
     * not equal value.
     */
    private AbstractMap.SimpleEntry<Node<E>, Boolean> findVectoredOrExactNodeWithQualification
            (Node<E> node, Object value, TreeState.Vector vector) {
        if (node == null) {
            return new AbstractMap.SimpleEntry<>(null, false);
        }

        int compareResult = compare(node.getValue(), value);
        if (compareResult == 0) {
            return new AbstractMap.SimpleEntry<>(node, true);
        }

        boolean isVectoredValue = (getVectorByCompareResult(compareResult) == vector);
        AbstractMap.SimpleEntry<Node<E>, Boolean> vectoredNode =
                findVectoredOrExactNodeWithQualification(node.getVectorSon(getVectorByCompareResult(compareResult)), value, vector);
        if (vectoredNode != null) {
            return vectoredNode;
        }
        if (isVectoredValue) {
            return new AbstractMap.SimpleEntry<>(node, false);
        }

        return new AbstractMap.SimpleEntry<>(null, false);
    }

    // Standard implementation is quite unoptimal
    @Override
    public boolean contains(Object o) {
        return findVectoredOrExactNodeWithQualification(o, getLeftVector()).getValue();
    }

    /*
    List of unsupported operations
     */

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear not supported");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("remove not supported");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("removeAll not supported");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("retainAll not supported");
    }

    /**
     * Class for iterating over tree in ascending order
     */
    private class UnbalancedTreeSetIterator implements Iterator<E> {
        /**
         * Current Node iterator is point to
         */
        Node<E> next;

        /**
         * If vector is LEFT, rightmost Node is the starting Node, next() moves position to the LEFT direction
         * vice versa for LEFT vector
         * Thus, RIGHT vector build ascending iterator, LEFT build descending
         */
        @NotNull
        private TreeState.Vector vector;

        /**
         * Version of Tree when iterator has been created. If differs from actual Tree's version, iterator is invalid
         */
        private int version;

        private UnbalancedTreeSetIterator(int version, @NotNull TreeState.Vector vector) {
            this.version = version;
            this.vector = vector;
            next = getRoot().getDeepest(vector.opposite());
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

            return next != null;
        }

        @Override
        @NotNull
        public E next() {
            checkVersion();

            if (!hasNext()) {
                throw new NoSuchElementException("No next element in tree");
            }

            var returnValue = next.value;
            next = next.getNextNode(vector);
            return returnValue;
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
         * Returns the next Node in order
         * Returns null if there is no such
         */
        @Nullable
        private Node<E> getNextNode(TreeState.Vector vector) {
            if (hasSon(vector)) {
                return sonAtVector(vector).getDeepest(vector.opposite());
            }

            if (getParent() == null) {
                return null;
            }

            if (isVectorSon(vector.opposite())) {
                return getParent();
            }

            return getParent().getNextNode(vector);
        }

        /**
         * Returns leftmost or rightmost (depending on vector value) Node in Node's subtree
         */
        @NotNull
        private Node<E> getDeepest(@NotNull TreeState.Vector vector) {
            if (hasSon(vector)) {
                return sonAtVector(vector).getDeepest(vector);
            }

            return this;
        }

        /**
         * Returns true if direction of moving from parent of this node to this node equals to vector
         */
        private boolean isVectorSon(@NotNull TreeState.Vector vector) {
            if (parent == null) {
                return false;
            }
            return parent.getVectorSon(vector) == this;
        }

        /**
         * Returns son in direction of the vector without null checks
         */
        @Nullable
        private Node<E> getVectorSon(@NotNull TreeState.Vector vector) {
            return sons[vector.ordinal()];
        }

        /**
         * Returns if Node has son in direction of vector
         */
        private boolean hasSon(@NotNull TreeState.Vector vector) {
            return getVectorSon(vector) != null;
        }
        /**
         * Get son in direction of vector, throws exception if this son does not exists (null)
         */
        @NotNull
        private Node<E> sonAtVector(@NotNull TreeState.Vector vector) {
            if (!hasSon(vector)) {
                throw new IllegalArgumentException("Node does not have " + vector.name() + " son");
            }

            //on the if above we checked return statement won't be null, but IDEA can't process it correctly
            return Objects.requireNonNull(getVectorSon(vector));
        }

        /**
         * Returns parent Node of the Node
         */
        @Nullable
        private Node<E> getParent() {
            return parent;
        }

        /**
         * Set son of direction of vector
         */
        private void setSon(Node<E> leftSon, @NotNull TreeState.Vector vector) {
            sons[vector.ordinal()] = leftSon;
        }
    }

    /**
     * Class for accessing to the tree in reversed order of the original one.
     * Does NOT copy state of the tree (leaves it null). That way we can guaranteed that trying to change state of
     * this tree refers to NullPointerException, therefore force to override getters and setters to use getters and
     * setters of the original tree, that way all changes in one of the tree will reflect another tree.
     * <p></p>
     * Using DescendingSet will simply returns original tree.
     * <p></p>
     * Reversed order of iteration in this tree is achieved via making RIGHT vector to be LEFT and vice versa by overriding
     * corresponding getters of right and left vectors.
     * <p></p>
     * Invalidation of iterators in one of the tree's will invalid iterators in other tree as well
     */
    private static class DescendingTreeSet<E> extends UnbalancedTreeSet<E> {
        /**
         * Link to the original tree.
         */
        UnbalancedTreeSet<E> superSet;

        DescendingTreeSet(UnbalancedTreeSet<E> ascendingSet) {
            superSet = ascendingSet;
        }

        @Override
        public int size() {
            return superSet.size();
        }

        @Override
        protected Comparator<? super E> getComparator() {
            return superSet.getComparator();
        }

        @Override
        protected void setComparator(Comparator<? super E> comparator) {
            superSet.setComparator(comparator);
        }

        protected Node<E> getRoot() {
            return superSet.getRoot();
        }

        protected void setRoot(Node<E> root) {
            superSet.setRoot(root);
        }

        protected int getSize() {
            return superSet.getSize();
        }

        protected int getTreeVersion() {
            return superSet.getTreeVersion();
        }

        protected void increaseSize() {
            superSet.increaseSize();
        }

        protected void upgradeVersion() {
            superSet.upgradeVersion();
        }

        protected TreeState.Vector getLeftVector() {
            return superSet.getRightVector();
        }

        protected TreeState.Vector getRightVector() {
            return superSet.getLeftVector();
        }
    }

    /**
     * Returns number of elements in tree
     */
    protected int getSize() {
        return treeState.size;
    }

    /**
     * Return current version of tree. Changes after modification.
     */
    protected int getTreeVersion() {
        return treeState.version;
    }

    /**
     * Increases size of the tree after adding new element
     */
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
     * Returns comparator that Set uses, or null if it does not.
     */
    @Nullable
    protected Comparator<? super E> getComparator() {
        return treeState.comparator;
    }

    /**
     * Set comparator to another, intended use only in constructor.
     */
    protected void setComparator(Comparator<? super E> comparator) {
        treeState.comparator = comparator;
    }

    /**
     * Returns root of the tree
     */
    protected Node<E> getRoot() {
        return treeState.root;
    }

    /**
     * Set root of the tree to another
     */
    protected void setRoot(Node<E> root) {
        treeState.root = root;
    }

    /**
     * Returns LEFT vector.
     */
    protected TreeState.Vector getLeftVector() {
        return TreeState.Vector.LEFT;
    }

    /**
     * Returns RIGHT vector.
     */
    protected TreeState.Vector getRightVector() {
        return TreeState.Vector.RIGHT;
    }

    /**
     * Returns LEFT vector if compareResult > 0, RIGHT if compareResult < 0, throws exception otherwise
     */
    private TreeState.Vector getVectorByCompareResult(int compareResult) {
        if (compareResult < 0) {
            return getRightVector();
        }

        if (compareResult > 0) {
            return getLeftVector();
        }

        throw new IllegalArgumentException("compareResult can't be zero");
    }
}