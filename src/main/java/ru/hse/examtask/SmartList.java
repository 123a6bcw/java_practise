package ru.hse.examtask;

import java.util.AbstractList;
import java.util.List;

public class SmartList<E> extends AbstractList<E> implements List<E> {
    int size;
    Node<E> root;

    @Override
    public E get(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void add(int index, E element) {

    }

    @Override
    public E remove(int index) {
        return null;
    }

    @Override
    public E set(int index, E element) {
        return null;
    }

    private class Node<E> {

    }
}