package ru.hse.kr5;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for simulating work with parking automata with several entrances.
 */
public class Parking {
    /**
     * Maximum number of car in the parking zone.
     */
    private int carLimit;

    /**
     * Number of entrances.
     */
    private int entrances;

    /**
     * Current number of car in the parking zone.
     */
    private AtomicInteger currentCars = new AtomicInteger(0);

    public Parking(int carLimit, int entrances) {
        if (entrances < 1 || carLimit < 1) {
            throw new IllegalArgumentException("Entrances or carlimit is too low (cannot be <=0 )");
        }

        this.carLimit = carLimit;
        this.entrances = entrances;
    }

    /**
     * Throws exception if entrynumber is not within parking bounds.
     */
    private void checkBounds(int entryNumber) {
        if (entryNumber < 0 || entryNumber >= entrances) {
            throw new IllegalArgumentException("entryNumber is out of bounds");
        }
    }

    /**
     * Returns true if car can enter the parking zone (and enters it).
     */
    public boolean registerCar(int entryNumber) {
        checkBounds(entryNumber);

        return arbitr();
    }

    /**
     * Handles car leaving the parking zone throught entryNumber exit.
     */
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

    /**
     * Returns true if parking zone can accept new car and accept this car if so.
     */
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