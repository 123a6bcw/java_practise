package ru.hse.HashTable;

/**
 * object in list.
 */
public class Node {
    private final int key;
    private String value;
    private Node next; /** next object in list. */

    public Node(int key, String value) {
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
        value = newValue;
    }

    public void setNext(Node newNext) {
        next = newNext;
    }

    public Node getNext() {
        return next;
    }
}
