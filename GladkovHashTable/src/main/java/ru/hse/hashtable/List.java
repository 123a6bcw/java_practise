package ru.hse.hashtable;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A singly-connected list with iterating option.
 */
public class List implements Iterable<StringPair> {
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
     * returns object for iterating over the list
     */
    public Iterator<StringPair> iterator() {
        return new ListIterator(head);
    }

    /**
     * returns true if list is empty, false otherwise.
     * works in O(1)
     */
    public boolean empty() {
        return head == null;
    }

    /**
     * @param key object's key
     * returns Node with given key or null if there is no such node
     * works in O(length)
     */
    private Node find(int key) {
        for (var x = head; x != null; x = x.getNext()) {
            if (x.getKey() == key) {
                return x;
            }
        }

        return null;
    }

    /**
     * @param key object's key
     * returns true if list contains object with given key, false otherwise
     * works in O(length)
     */
    public boolean contains(int key) {
        return find(key) != null;
    }

    /**
     * @param key object's key
     * returns value of object with given key or null if there is no such object
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
     * returns old value of object with given key, null if there wasn't such object
     * works in O(length)
     */
    public String insert(int key, String value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

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
    private void push(Node node) {
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
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        Node a = new Node(key, value);
        push(a);
    }

    /**
     * @param pair pair of object's key and value
     * push object with given key and value (packed inside StringPair) to the end of the list
     * works in O(1)
     */
    public void push(StringPair pair) {
        push(pair.getKey(), pair.getValue());
    }

    /**
     * @param key object's key
     * removes object with given key from the list
     * returns value of deleted object or null if object wasn't found
     * works in O(length)
     */
    public String remove(int key) {
        Node prev = null; //previous Node in list, cause we don't store left connections
        for (var x = head; x != null; x = x.getNext()) {
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
    private static class ListIterator implements Iterator<StringPair> {
        /**
         * current element in the iterating process
         */
        private Node current;

        /**
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
         * returns next object in the list.
         */
        public StringPair next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node returnValue = current;
            current = current.getNext();
            return returnValue.toStringPair();
        }

        /*
        java tells me I need to add this method
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * object in list.
     */
    public class Node {
        private final int key;
        private String value;
        private Node next; /** next object in list. */

        public Node(int key, String value) {
            if (value == null) {
                throw new IllegalArgumentException("value cannot be null");
            }

            this.key = key;
            this.value = value;
            this.next = null;
        }

        public int getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String newValue) {
            if (newValue == null) {
                throw new IllegalArgumentException("newValue cannot be null");
            }

            value = newValue;
        }

        public void setNext(Node newNext) {
            next = newNext;
        }

        public Node getNext() {
            return next;
        }

        public boolean hasNext() {
            return next != null;
        }

        public StringPair toStringPair() {
            return new StringPair(key, value);
        }
    }
}