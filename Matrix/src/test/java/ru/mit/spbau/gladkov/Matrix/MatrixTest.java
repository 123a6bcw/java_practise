package ru.mit.spbau.gladkov.Matrix;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MatrixTest {
    protected final ByteArrayOutputStream output = new ByteArrayOutputStream();

    /**
     *Перехватывает поток вывода, чтобы его можно было сравнить с ожидаемым выводом.
     */

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(output));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }

    @org.junit.Test
    public void print1x1() throws Exception {
        int[][] a = {{1}};
        Matrix.print(a);
        assertEquals("1" + System.lineSeparator(), output.toString());
    }

    @org.junit.Test
    public void print3x3() throws Exception {
        int[][] a = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        Matrix.print(a);
        assertEquals("1 2 3" + System.lineSeparator() + "4 5 6" + System.lineSeparator() + "7 8 9" + System.lineSeparator(), output.toString());
    }

    @org.junit.Test
    public void spiral1x1() throws Exception {
        int[][] a = {{1}};
        Matrix.spiral(a);
        assertEquals("1" + System.lineSeparator(), output.toString());
    }

    @org.junit.Test
    public void spiral3x3() throws Exception {
        int[][] a = {{1,2,3}, {4, 5, 6}, {7, 8, 9}};
        Matrix.spiral(a);
        assertEquals("7 8 9" + System.lineSeparator() + "6 1 2" + System.lineSeparator() + "5 4 3" + System.lineSeparator(), output.toString());
    }

    @org.junit.Test
    public void sortRows1x1() throws Exception {
        int[][] a = {{1}};
        Matrix.sortRows(a);
        Matrix.print(a);
        assertEquals("1" + System.lineSeparator(), output.toString());
    }

    @org.junit.Test
    public void sortRows3x3() throws Exception {
        int[][] a = {{2, 1, 3}, {5, 4, 6}, {8, 7, 9}};
        Matrix.sortRows(a);
        Matrix.print(a);
        assertEquals("1 2 3" + System.lineSeparator() + "4 5 6" + System.lineSeparator() + "7 8 9" + System.lineSeparator(), output.toString());
    }

    @org.junit.Test
    public void transpose1x1() throws Exception {
        int[][] a = {{1}};
        Matrix.transpose(a);
        Matrix.print(a);
        assertEquals("1" + System.lineSeparator(), output.toString());
    }

    @org.junit.Test
    public void transpose3x3() throws Exception {
        int[][] a = {{1,2,3}, {4, 5, 6}, {7, 8, 9}};
        Matrix.transpose(a);
        Matrix.print(a);
        assertEquals("1 4 7" + System.lineSeparator() + "2 5 8" + System.lineSeparator() + "3 6 9" + System.lineSeparator(), output.toString());
    }
}