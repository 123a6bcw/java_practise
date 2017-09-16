package ru.mit.spbau.HashTable;

import java.util.NoSuchElementException;
import java.util.Iterator;

/**
 * a simply-connected list with an iterating option.
 */

public class List implements Iterable<Node> {
    private Node head; /** first object in table */
    private Node tail; /** last object in table */

    List() {
        head = null;
        tail = null;
    }

    /** creates an object for iterating over list */
    public Iterator<Node> iterator() {
        return new ListIterator(head);
    }

    /** checks if list is empty. */
    public boolean empty() {
        return head == null;
    }

    /** checks if list containg an object with such key. */
    public boolean contains(int key) {
        for (Node x : this) { //iterating over this list
            if (x.key == key) {
                return true;
            }
        }

        return false;
    }

    /** get an object with such key or returning null if he haven't found it */
    public String get(int key) {
        for (Node x : this) {
            if (x.key == key) {
                return x.value;
            }
        }

        return null;
    }

    /**
     * inserts an object into list if there wasn't an object with such key
     * otherwise overrides it's value and returns an old value
     */
    public String insert(int key, String value) {
        for (Node x : this) {
            if (x.key == key) {
                String returnValue = x.value;
                x.value = value;
                return returnValue;
            }
        }

        push(key, value);
        return null;
    }

    /** adds new object to the end of the list */
    public void push(Node a) {
        if (empty()) {
            head = a;
            tail = a;
            return;
        }

        tail.next = a;
        tail = a;
        a.next = null;
    }

    /** creates a Node before pushing it to the end */
    public void push(int key, String value) {
        Node a = new Node(key, value);
        push(a);
    }

    /** removes an object with such key from the table, or returns null if there isn't such one */
    public String remove(int key) {
        Node prev = null; //previous Node in list from current one
        for (Node x : this) {
            if (x.key == key) {
                if (prev != null) { //so x isn't the first one
                    prev.next = x.next;

                    if (x == tail) { //so we just deleted tail
                        tail = prev;
                    }
                } else {
                    if (x == tail && x == head) { //so list is now empty
                        head = null;
                        tail = null;
                    } else { //else we just deleted the head of the list
                        head = x.next;
                    }
                }
                return x.value;
            }

            prev = x;
        }

        return null;
    }

    /** makes list empty */
    public void clear() {
        head = null;
        tail = null;
    }

    /** class for iterating over list */
    private static class ListIterator implements Iterator<Node> {
        private Node current; /** position of the next object we will get from the list */

        public ListIterator(Node head) {
            current = head;
        }

        /** checking if there is next object */
        public boolean hasNext() {
            return current != null;
        }

        /** getting the next object from the list and moving current position */
        public Node next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node returnValue = current;
            current = current.next;
            return returnValue;
        }

        /** java tells I need to add this method */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}