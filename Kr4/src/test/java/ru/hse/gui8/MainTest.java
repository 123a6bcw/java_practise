package ru.hse.gui8;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private void testEvenN(int n) {
        Main.GridState gridState = new Main.GridState(n);
        int[][] number = gridState.getNumbers();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == n/2 && j == n/2) {
                    continue;
                }

                assertTrue(number[i][j] >= 0 && number[i][j] < n);
            }
        }

        for (int k = 0; k < n*n/2; k++) {
            int cellsWithK = 0;

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (number[i][j] == k) {
                        cellsWithK++;
                    }
                }
            }

            assertEquals(2, cellsWithK);
        }
    }

    @Test
    void correctNumbersForNEqualTo2() {
        testEvenN(2);
    }

    @Test
    void correctNumbersForNEqualTo4() {
        testEvenN(4);
    }

    @Test
    void correctNumbersForNEqualTo3() {
        testEvenN(3);
    }

    @Test
    void correctNumbersForNEqualTo5() {
        testEvenN(5);
    }
}