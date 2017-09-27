package ru.mit.spbau.gladkov.Matrix;

import java.util.Arrays;

/**
 * Copyright (c) 2017 Gladkov Alexander
 *
 * Class what can print int[][] in spiral order, transpose int[][] and
 * sort rows of int[][] by first element of row
 */

public class Matrix {
    public static void main(String args[]) {
        int[][] a = new int[9][9];
        int q = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                a[i][j] = q;
                q++;
            }
        }

        sortRows(a);
        print(a);
    }

    /**
     * Print int[][] in spiral order
     *
     * @param a matrix n*n, where n is odd
     */

    public static int[][] spiral(int a[][]) {
        int vectorRow[] =  {0, 1, 0, -1};
        /**
         * vector of movement on rows in order: right, down, left, up
         */

        int vectorColumn[] = {1, 0, -1, 0};
        /**
         * vector of movement on columns in the same order
         */

        int directions = 4;
        /**
         * number of different directions in the matrix
         */

        int[][] out = new int[a.length][a.length];

        int currentRow = a.length / 2;
        int currentColumn = a.length / 2;
        /**
         * positions in result spiral matrix
         */

        int currentStep = 1;
        /**
         * length of current spiral coil
         */

        int leftSteps = currentStep;
        /**
         * number of left cells in current spiral coil
         */

        int currentVector = 0;
        /**
         * direction of current spiral coil
         */

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                out[currentRow][currentColumn] = a[i][j];

                currentRow += vectorRow[currentVector];
                currentColumn += vectorColumn[currentVector];
                leftSteps--;

                if (leftSteps == 0) {
                    currentVector++;
                    if (currentVector % 2 == 0) {
                        currentStep++;
                    }

                    if (currentVector == directions) {
                        currentVector = 0;
                    }

                    leftSteps = currentStep;
                }
            }
        }

        print(out);
        return out;
    }

    /**
     * prints matrix
     */

    public static void print(int a[][]) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                System.out.print(a[i][j]);

                if (j + 1 < a.length) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    /**
     * sorts rows of matrix by the first element in rows
     */

    public static void sortRows(int a[][]) {
        transpose(a);

        Arrays.sort(a, (int[] column1, int[] column2) -> {
            return Integer.compare(column1[0], column2[0]);
        });

        transpose(a);
    }

    /**
     * transposes matrix
     */

    public static void transpose(int a[][]) {
        for (int i = 0; i < a.length; i++) {
            for (int j = i + 1; j < a.length; j++) {
                int swap = a[i][j];
                a[i][j] = a[j][i];
                a[j][i] = swap;
            }
        }
    }
}
