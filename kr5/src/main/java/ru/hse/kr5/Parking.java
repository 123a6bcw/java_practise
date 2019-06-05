package ru.hse.kr5;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 */
public class Parking {
    private int carLimit;
    private int entrances;

    private Thread[] entryThreads;
    private AtomicInteger[] carsInQueue;

    private AtomicInteger currentCars = new AtomicInteger(0);

    public Parking(int carLimit, int entrances) {
        if (entrances < 1 || carLimit < 1) {
            throw new IllegalArgumentException("Entrances or carlimit is too low (cannot be <=0 )");
        }

        this.carLimit = carLimit;
        this.entrances = entrances;

        entryThreads = new Thread[entrances];
        carsInQueue = new AtomicInteger[entrances];
        for (int i = 0; i < entrances; i++) {
            carsInQueue[i] = new AtomicInteger(0);
        }
    }

    private void checkBounds(int entryNumber) {
        if (entryNumber < 0 || entryNumber >= entrances) {
            throw new IllegalArgumentException("entryNumber is out of bounds");
        }
    }

    public boolean registerCar(int entryNumber) {
        checkBounds(entryNumber);

        return arbitr();
    }

    public void unregisterCar(int entryNumber) {
        checkBounds(entryNumber);

        int result = currentCars.decrementAndGet();
        if (result >= carLimit) {
            currentCars.compareAndSet(result, carLimit - 1);
        }

        if (result < 0) {
            currentCars.compareAndSet(result, 0);
        }
    }

    private boolean arbitr() {
        int result = currentCars.incrementAndGet();

        if (result <= 0) {
            currentCars.compareAndSet(result, 1);
        }

        if (result > carLimit) {
            currentCars.compareAndSet(result, carLimit);
            return false;
        } else {
            return true;
        }
    }
}