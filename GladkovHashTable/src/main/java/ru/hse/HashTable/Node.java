package ru.hse.HashTable;

/**
 * object in list.
 */
/*
 Хочется сделать этот класс вложенным в List, но тогда я не могу написать "List implements Iterable<Node>"
 т.к. Java ещё не знает про такой класс. Можно либо объявить Node в файле с классом List вне класса List и сделать ему
 пакетную видимость (т.к. private и protected в таком случае нельзя), но вы говорили, что
 пакетную видимость лучше стараться не использовать, либо сделать оба класса приватными внутри HashTable,
 но так как-то некрасиво. Это нормальные решения, или есть более адекватные?
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
