package ru.hse.kr5;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ParkingTest {

    private final Object currentCarLock = new Object();
    private int currentCar;

    @Test
    void everythingWorksWithoutRaces() throws InterruptedException {
        final int carLimit = 20;
        final int en = 10;
        Parking parking = new Parking(carLimit, en);

        Thread[] threads = new Thread[en];
        for (int i = 0; i < en; i++) {
            final int localI = i;
            final Random random = new Random();

            threads[i] = new Thread(() -> {
                while (!Thread.interrupted()) {
                    if (random.nextInt() % 2 == 0) {
                        int localCurrentCar;

                        synchronized (currentCarLock) {
                            localCurrentCar = currentCar;

                            if (currentCar < carLimit) {
                                currentCar++;
                            }

                            boolean result = parking.registerCar(localI);
                            if (localCurrentCar < carLimit != result) {
                                System.out.print(localCurrentCar + " ");
                                System.out.print(carLimit + " ");
                                System.out.println(result);
                                fail();
                            }
                        }
                    } else {
                        synchronized (currentCarLock) {
                            if (currentCar > 0) {
                                currentCar--;
                            }

                            parking.unregisterCar(localI);
                        }
                    }
                }
            });
        }


        for (int i = 0; i < en; i++) {
            threads[i].start();
        }

        Thread.sleep(3000);

        for (int i = 0; i < en; i++) {
            threads[i].interrupt();
        }
    }

    @Test
    void everythingWorksWithRaces() throws InterruptedException {
        final int carLimit = 20;
        final int en = 10;
        Parking parking = new Parking(carLimit, en);

        Thread[] threads = new Thread[en];
        for (int i = 0; i < en; i++) {
            final int localI = i;
            final Random random = new Random();

            threads[i] = new Thread(() -> {
                while (!Thread.interrupted()) {
                    if (random.nextInt() % 2 == 0) {
                        int localCurrentCar;

                        synchronized (currentCarLock) {
                            localCurrentCar = currentCar;

                            if (currentCar < carLimit) {
                                currentCar++;
                            }

                            boolean result = parking.registerCar(localI);
                            if (localCurrentCar < carLimit != result) {
                                System.out.print(localCurrentCar + " ");
                                System.out.print(carLimit + " ");
                                System.out.println(result);
                                fail();
                            }
                        }
                    } else {
                        synchronized (currentCarLock) {
                            if (currentCar > 0) {
                                currentCar--;
                            }

                            parking.unregisterCar(localI);
                        }
                    }
                }
            });
        }


        for (int i = 0; i < en; i++) {
            threads[i].start();
        }

        Thread.sleep(3000);

        for (int i = 0; i < en; i++) {
            threads[i].interrupt();
        }
    }
}