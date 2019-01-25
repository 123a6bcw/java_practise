package ru.hse.hashtable;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A singly-connected list with iterating option.
 */
public class List implements Iterable<Node> {
    /**
     * first object in list.
     */
    private Node head;

    /**
     * last object in list.
     */
    private Node tail;

    List() {
        head = null; //I know they would initialise anyway, but I prefer to do it manually
        tail = null;
    }

    /**
     * @return object for iterating over the list
     */
    public Iterator<Node> iterator() {
        return new ListIterator(head);
    }

    /**
     * @return true if list is empty, false otherwise.
     * works in O(1)
     */
    public boolean empty() {
        return head == null;
    }

    /**
     * @param key object's key
     * @return Node with given key or null if there is no such node
     * works in O(length)
     */
    private Node find(int key) {
        for (Node x : this) {
            if (x.getKey() == key) {
                return x;
            }
        }

        return null;
    }

    /**
     * @param key object's key
     * @return true if list contains object with given key, false otherwise
     * works in O(length)
     */
    public boolean contains(int key) {
        return find(key) != null;
    }

    /**
     * @param key object's key
     * @return value of object with given key or null if there is no such object
     * works in O(length)
     */
    public String get(int key) {
        Node found = find(key);
        if (found != null) {
            return found.getValue();
        }

        return null;
    }

    /**
     * @param key object's key
     * @param value object's value
     * inserts new object with given key and value into list if there wasn't an object with such key
     * otherwise overrides value of object with given key
     * @return old value of object with given key, null if there wasn't such object
     * works in O(length)
     */
    public String insert(int key, String value) {
        Node found = find(key);
        if (found != null) {
            String returnValue = found.getValue();
            found.setValue(value);
            return returnValue;
        }

        push(key, value);
        return null;
    }

    /**
     * @param node object with key and value
     * adds new object to the end of the list
     * works in O(1) and does not check existence of object's with same key
     */
    protected void push(Node node) {
        if (empty()) {
            head = node;
            tail = node;
            return;
        }

        tail.setNext(node);
        tail = node;
        node.setNext(null);
    }

    /**
     * @param key object's key
     * @param value object's value
     * push object with given key and value to the end of the list
     * works in O(1)
     */
    public void push(int key, String value) {
        Node a = new Node(key, value);
        push(a);
    }

    /**
     * @param key object's key
     * removes object with given key from the list
     * @return value of deleted object or null if object wasn't found
     * works in O(length)
     */
    public String remove(int key) {
        Node prev = null; //previous Node in list, cause we don't store left connections
        for (Node x : this) {
            if (x.getKey() == key) {
                if (prev != null) { //so x isn't the first one
                    prev.setNext(x.getNext());

                    if (x == tail) { //so we just deleted tail
                        tail = prev;
                    }
                } else {
                    if (x == tail && x == head) { //so list is now empty
                        clear();
                    } else { //else we just deleted the head of the list
                        head = x.getNext();
                    }
                }

                return x.getValue();
            }

            prev = x;
        }

        return null;
    }

    /**
     * delete all elements from the list
     * works in O(1)
     */
    public void clear() {
        head = null;
        tail = null;
    }

    /**
     * class for iterating over list
     */
    private static class ListIterator implements Iterator<Node> {
        /**
         * current element in the iterating process
         */
        private Node current;

        /*
        position of the next object we will get from the list
         */
        private ListIterator(Node head) {
            current = head;
        }

        /**
         * checking if there is next object.
         */
        public boolean hasNext() {
            return current != null;
        }

        /**
         * getting the next object from the list and moving current position.
         * @return next object in the list.
         */
        public Node next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node returnValue = current;
            current = current.getNext();
            return returnValue;
        }

        /*
        java tells me I need to add this method
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}