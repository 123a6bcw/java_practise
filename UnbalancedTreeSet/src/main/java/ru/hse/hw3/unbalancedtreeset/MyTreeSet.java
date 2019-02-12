package ru.hse.hw3.unbalancedtreeset;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Interface for sorted type of sets that allows to find first element that lower than given one etc and iterates over set
 * in descending order
 */
public interface MyTreeSet<E> extends Set<E> {
    /** {@link TreeSet#descendingIterator()} **/
    Iterator<E> descendingIterator();

    /** {@link TreeSet#descendingSet()} **/
    MyTreeSet<E> descendingSet();

    /** {@link TreeSet#first()} **/
    E first();

    /** {@link TreeSet#last()} **/
    E last();


    /** {@link TreeSet#lower(E)} **/
    E lower(E e);

    /** {@link TreeSet#floor(E)} **/
    E floor(E e);


    /** {@link TreeSet#ceiling(E)} **/
    E ceiling(E e);

    /** {@link TreeSet#higher(E)} **/
    E higher(E e);
}