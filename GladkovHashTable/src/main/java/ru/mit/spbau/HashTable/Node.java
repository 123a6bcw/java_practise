package ru.mit.spbau.HashTable;

/** an object in list */
public class Node {
    public int key;
    public String value;
    public Node next; /** next object in list */

    Node(int key, String value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }
}
