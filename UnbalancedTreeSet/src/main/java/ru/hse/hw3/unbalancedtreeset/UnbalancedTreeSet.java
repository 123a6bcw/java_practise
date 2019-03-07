package ru.hse.hw3.unbalancedtreeset;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Elements of type E set collection based on BST data structure
 * All values in the right subtree of the node is always strictly greater than value in the node, all values in the left
 * subtree is always strictly less.
 */
public class UnbalancedTreeSet<E> extends AbstractCollection<E> implements MyTreeSet<E> {
    /**
     * Class for storing tree's state (basically all mutable fields). See DescendingTreeSet to see details of necessity in using this class
     */
    /*
    Parametrized because root has to be parametrized.
    Root IS mutable field because it may be null.
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
         * Root of the tree.
         */
        @Nullable
        private Node<E> root;

        /**
         * Comparator of elements inside the set.
         */
        @Nullable
        private Comparator<? super E> comparator;
    }

    /**
     * Stored current tree state.
     */
    @NotNull
    private TreeState<E> treeState;

    /**
     * Enum for moving to either left or right son of the Node.
     */
    private enum Vector {
        LEFT, RIGHT;

        /**
         * Returns vector opposite to this one.
         */
        private Vector opposite() {
            if (this == LEFT) {
                return Vector.RIGHT;
            } else {
                return Vector.LEFT;
            }
        }
    }

    /**
     * If lowerVector is LEFT, tree assumes that left son of the node contains lower elements, otherwise assumes
     * it's right son. Uses only for VIEWING options (one that does not changes state of the tree).
     * Basically, it is LEFT for default set and RIGHT for descendingSet.
     */
    @NotNull
    private Vector lowerVector = Vector.LEFT;

    /**
     * Creates new tree set, all elements will be sorted using custom comparator
     */
    public UnbalancedTreeSet(@NotNull Comparator<? super E> comparator) {
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
    }

    /**
     * Compare objects using given comparator or by asuming objects in set are comparable.
     * If both comparator not specified and objects aren't comparable, throws ClassCastException.
     * Objects cannot be null because Null can't be contained in given set.
     */
    /*
    Causes UncheckedCastWarning because cast may be wrong if user gave set elements with no comparable interface
    and didn't specify comparator
     */
    @SuppressWarnings("unchecked")
    private int compare(@NotNull Object o1, @NotNull Object o2) {
        if (getComparator() == null) {
            return ((Comparable<? super E>)o1).compareTo((E) o2);
        } else {
            return getComparator().compare((E) o1, (E)o2);
        }
    }

    /**
     * If set does not contain element with the same value, adds given element to the set and returns true.
     * Otherwise does nothing and returns false.
     */
    @Override
    public boolean add(@NotNull E e) {
        if (getRoot() == null) {
            setRoot(new Node<>(e, null));
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
    private boolean add(@NotNull E e, @NotNull Node<E> node) {
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
     * Creates iterator for iterating over elements in set in the natural order.
     */
    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new UnbalancedTreeSetIterator(getTreeVersion(), lowerVector.opposite());
    }

    /**
     * Creates iterator for iterating over elements in set in the reversed order.
     */
    @NotNull
    @Override
    public Iterator<E> descendingIterator() {
        return new UnbalancedTreeSetIterator(getTreeVersion(), lowerVector);
    }

    /**
     * Returns size of the tree. Works in O(1) as tree maintains it's size
     */
    @Override
    public int size() {
        return treeState.size;
    }

    /**
     * Creates the same tree set but in reversed order. Changes state of this tree reflects reversed tree and vice versa,
     * including invalidating iterators in one of the tree's.
     */
    @NotNull
    @Override
    public UnbalancedTreeSet<E> descendingSet() {
        return new DescendingTreeSet();
    }

    /**
     * Returns first element in set.
     * Throws NoSuchElementException if there is no elements in Set.
     */
    @NotNull
    @Override
    public E first() {
        return iterator().next();
    }

    /**
     * Returns last element in set.
     * Throws NoSuchElementException if there is no elements in Set.
     */
    @NotNull
    @Override
    public E last() {
        return descendingIterator().next();
    }

    /**
     * Finds greatest element lower than given one, or null if there is no such one.
     */
    @Nullable
    @Override
    public E lower(@NotNull E e) {
        return findVectoredValue(e, lowerVector);
    }

    /**
     * Finds greatest element lower or equal to given one, or null if there is no such one
     */
    @Nullable
    @Override
    public E floor(@NotNull E e) {
        return findVectoredOrExactValue(e, lowerVector);
    }

    /**
     * Find lowest element greater or equal to given one, or null if there is no such one
     */
    @Nullable
    @Override
    public E ceiling(@NotNull E e) {
        return findVectoredOrExactValue(e, lowerVector.opposite());
    }

    /**
     * Find lowest element greater than given one, or null if there is no such one
     */
    @Nullable
    @Override
    public E higher(@NotNull E e) {
        return findVectoredValue(e, lowerVector.opposite());
    }

    /**
     * findVectoredValue started from the root, other parameters is the same.
     */
    @Nullable
    private E findVectoredValue(@NotNull Object value, @NotNull Vector vector) {
        return findVectoredValue(getRoot(), value, vector);
    }

    /**
     * Returns value of findVectoredNode with same parameters, or null if result is null
     */
    @Nullable
    private E findVectoredValue(@Nullable Node<E> node, @NotNull Object value, @NotNull Vector vector) {
        Node<E> nodeResult = findVectoredNode(node, value, vector);
        if (nodeResult == null) {
            return null;
        }
        return nodeResult.getValue();
    }

    /**
     * findVectoredOrExactValue started from the root, other parameters is the same.
     */
    @Nullable
    private E findVectoredOrExactValue(@NotNull Object value, @NotNull Vector vector) {
        return findVectoredOrExactValue(getRoot(), value, vector);
    }

    /**
     * Returns value of node found in findVectoredOrExactValueWithQualification with the same parameters, or
     * null if that node is null.
     */
    @Nullable
    private E findVectoredOrExactValue(@Nullable Node<E> node, @NotNull Object value, @NotNull Vector vector) {
        Node<E> nodeResult = findVectoredOrExactNodeWithQualification(node, value, vector).getNode();
        if (nodeResult == null) {
            return null;
        }
        return nodeResult.getValue();
    }

    /**
     * Class for storing qualified results of finding nodes, there isEqual is true when found Node has value equal to
     * requested
     */
    private static class QualifiedResult<E> {
        private Node<E> node;
        private boolean isEqual;

        QualifiedResult(Node<E> node, boolean isEqual) {
            this.node = node;
            this.isEqual = isEqual;
        }

        private Node<E> getNode() {
            return node;
        }

        private boolean isEqual() {
            return isEqual;
        }
    }

    /**
     * RIGHT vector correspond to finding greater element (aka moving to the right son), LEFT vice versa
     * <p></p>
     * Finds Node with lowest value that strictly greater (greatest lower) than given value in given node's subtree
     * <p></p>
     * Returns null if there is no such Node
     */
    @Nullable
    private Node<E> findVectoredNode(@Nullable Node<E> node, @NotNull Object value, @NotNull Vector vector) {
        QualifiedResult<E> qualifiedResult = findVectoredOrExactNodeWithQualification(node, value, vector);
        if (!qualifiedResult.isEqual()) {
            return qualifiedResult.getNode();
        }
        return qualifiedResult.getNode().getNextNode(vector);
    }

    /**
     * RIGHT vector corresponds to finding greater element (aka moving to the right son), LEFT vice versa
     * <p></p>
     * First element in pair is Node with lowest value that strictly greater (greatest lower) than given value in given node's subtree,
     * null if there is no such Node
     * <p></p>
     * Second element in pair is true if value in found node is equal to given one, false if found node is null or have
     * not equal value.
     */
    @NotNull
    private QualifiedResult<E> findVectoredOrExactNodeWithQualification
            (@Nullable Node<E> node, @NotNull Object value, @NotNull Vector vector) {
        if (node == null) {
            return new QualifiedResult<>(null, false);
        }

        int compareResult = compare(node.getValue(), value);
        if (compareResult == 0) {
            return new QualifiedResult<>(node, true);
        }

        boolean isVectoredValue = (getVectorByCompareResult(compareResult) == vector.opposite());
        QualifiedResult<E> vectoredNode =
                findVectoredOrExactNodeWithQualification(node.getVectorSon(getVectorByCompareResult(compareResult).opposite()), value, vector);
        if (vectoredNode.getNode() != null) {
            return vectoredNode;
        }
        if (!isVectoredValue) {
            return new QualifiedResult<>(node, false);
        }

        return new QualifiedResult<>(null, false);
    }

    /**
     * Finds Node with value equals to given value. Returns null if there is no such Node.
     */
    @Nullable
    private Node<E> findExactNode(@NotNull Object value) {
        QualifiedResult<E> result = findVectoredOrExactNodeWithQualification(getRoot(), value, Vector.LEFT);
        if (result.isEqual()) {
            return result.getNode();
        } else {
            return null;
        }
    }

    /**
     * Returns true if set contains given object.
     */
    @Override
    public boolean contains(@NotNull Object o) {
        return findExactNode(o) != null;
    }

    /**
     * Clears all objects from the set.
     */
    @Override
    public void clear() {
        setRoot(null);
        clearSize();
        upgradeVersion();
    }

    /**
     * Removes given object from the set.
     * Returns true if object was in set and was removed, false otherwise.
     */
    @Override
    public boolean remove(@NotNull Object o) {
        Node<E> removeNode = findExactNode(o);
        if (removeNode == null) {
            return false;
        }

        Node<E> leftSon = removeNode.getVectorSon(Vector.LEFT);
        Node<E> rightSon = removeNode.getVectorSon(Vector.RIGHT);
        if (leftSon == null) {
            leftSon = rightSon;
        } else if (rightSon != null) {
            rightSon.getDeepest(Vector.LEFT).setSon(leftSon.getVectorSon(Vector.RIGHT), Vector.LEFT);
            leftSon.setSon(rightSon, Vector.RIGHT);
        }

        if (removeNode.getParent() == null) {
            //True only for root node
            setRoot(leftSon);
            if (leftSon != null) {
                leftSon.setParent(null);
            }
        } else {
            removeNode.getParent().setSon(leftSon, getVectorByPredicate(removeNode.isVectorSon(Vector.LEFT)));
        }

        decreaseSize();
        upgradeVersion();
        return true;
    }

    /**
     * Class for iterating over tree in given (ascending or descending) order
     */
    private class UnbalancedTreeSetIterator implements Iterator<E> {
        /**
         * Current Node iterator is point to
         */
        private Node<E> next;

        /**
         * If vector is LEFT, rightmost Node is the starting Node, next() moves position to the LEFT direction
         * vice versa for RIGHT vector
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
            if (getRoot() == null) {
                next = null;
            } else {
                next = getRoot().getDeepest(vector.opposite());
            }
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

        /**
         * Returns whether iterator contains next object.
         * Throws ConcurrentModificationException() if iterator is invalid after tree's modification
         *
         */
        @Override
        public boolean hasNext() {
            checkVersion();

            return next != null;
        }

        /**
         * Returns next object in iterator.
         * Throws NoSuchElementException if there is no next element
         * Throws ConcurrentModificationException() if iterator is invalid after tree's modification
         */
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
        @Nullable
        private Node<E> leftSon;

        /**
         * Right son Node in tree
         */
        @Nullable
        private Node<E> rightSon;

        /**
         * Parent Node in tree
         * Null only for root Node
         */
        @Nullable
        private Node<E> parent;

        private Node(@NotNull E value, @Nullable Node<E> parent) {
            this.value = value;
            this.parent = parent;
            leftSon = null;
            rightSon = null;
        }

        /**
         * Returns value stored in Node. Cannot be null.
         */
        @NotNull
        private E getValue() {
            return value;
        }

        /**
         * Returns the next Node in order
         * Returns null if there is no such
         */
        @Nullable
        private Node<E> getNextNode(@NotNull Vector vector) {
            if (hasSon(vector)) {
                return sonAtVector(vector).getDeepest(vector.opposite());
            }

            var resultNode = this;
            while (resultNode != null && !resultNode.isVectorSon(vector.opposite())) {
                resultNode = resultNode.getParent();
            }

            if (resultNode == null) {
                return null;
            }

            return resultNode.getParent();
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
         * Returns son in direction of the vector without null checks
         */
        @Nullable
        private Node<E> getVectorSon(@NotNull Vector vector) {
            switch (vector) {
                case LEFT:
                    return leftSon;
                case RIGHT: default:
                    return rightSon;
            }
        }

        /**
         * Returns if Node has son in direction of vector
         */
        private boolean hasSon(@NotNull Vector vector) {
            return getVectorSon(vector) != null;
        }

        /**
         * Get son in direction of vector, throws exception if this son does not exists (equals to null)
         */
        @NotNull
        private Node<E> sonAtVector(@NotNull Vector vector) {
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
         * Sets parent of the Node
         */
        private void  setParent(@Nullable Node<E> parent) {
            this.parent = parent;
        }

        /**
         * Set son of direction of vector
         */
        private void setSon(@Nullable Node<E> son, @NotNull Vector vector) {
            switch (vector) {
                case LEFT:
                    leftSon = son;
                    break;
                case RIGHT: default:
                    rightSon = son;
                    break;
            }

            if (son != null) {
                son.setParent(this);
            }
        }
    }

    /**
     * Class that creates tree that absolutely equals to the original tree, but all VIEWING operations are being made in
     * reversed order to the original set. It is achieved by reversing corresponding way vector and sharing the same
     * state of the tree between two sets.
     */
    private class DescendingTreeSet extends UnbalancedTreeSet<E> {
        /**
         * Creates set that shares treeState of the original tree, but changes way vector in viewing options.
         */
        private DescendingTreeSet() {
            this.setTreeState(getTreeState());
            this.setLowerVector(Vector.RIGHT);
        }

        /**
         * Returns original set. In this way using descendingSet() multiple times won't make more than one extra object.
         */
        @Override
        @NotNull
        public UnbalancedTreeSet<E> descendingSet() {
            return UnbalancedTreeSet.this;
        }
    }

    /**
     * Changes the way tree assumes the lower element is relatively to the parent node
     */
    protected void setLowerVector(@NotNull Vector vector) {
        lowerVector = vector;
    }

    /*
    I implemented all (necessary) setters and getters so I wouldn't have to write treeState.something each time I want to use fields.
     */

    /**
     * Changes size to zero.
     */
    private void clearSize() {
        treeState.size = 0;
    }

    /**
     * Return current version of tree. Changes after modification.
     */
    private int getTreeVersion() {
        return treeState.version;
    }

    /**
     * Increases size of the tree after adding new element
     */
    private void increaseSize() {
        treeState.size++;
    }

    /**
     * Decreases size of the tree after removing element
     */
    private void decreaseSize() {
        treeState.size--;
    }

    /**
     * Increases current tree's version after modifications of the tree.
     * May overflow, but that's ok.
     */
    private void upgradeVersion() {
        treeState.version++;
    }

    /**
     * Returns comparator that Set uses, or null if it does not.
     */
    @Nullable
    private Comparator<? super E> getComparator() {
        return treeState.comparator;
    }

    /**
     * Set comparator to another, intended use only in constructor.
     */
    private void setComparator(@NotNull Comparator<? super E> comparator) {
        treeState.comparator = comparator;
    }

    /**
     * Returns root of the tree
     */
    @Nullable
    private Node<E> getRoot() {
        return treeState.root;
    }

    /**
     * Set root of the tree to another
     */
    private void setRoot(@Nullable Node<E> root) {
        treeState.root = root;
    }

    /**
     * Returns state of the tree.
     */
    @NotNull
    private TreeState<E> getTreeState() {
        return treeState;
    }

    /**
     * Sets state of the tree.
     */
    protected void setTreeState(@NotNull TreeState<E> treeState) {
        this.treeState = treeState;
    }

    /**
     * Returns LEFT vector if compareResult > 0, RIGHT if compareResult < 0, throws exception otherwise
     */
    @NotNull
    private Vector getVectorByCompareResult(int compareResult) {
        if (compareResult < 0) {
            return Vector.LEFT;
        }

        if (compareResult > 0) {
            return Vector.RIGHT;
        }

        throw new IllegalArgumentException("compareResult can't be zero");
    }

    /**
     * Returns LEFT vector if predicate is true, RIGHT otherwise
     */
    @NotNull
    private Vector getVectorByPredicate(boolean predicate) {
        return (predicate) ? Vector.LEFT : Vector.RIGHT;
    }
}