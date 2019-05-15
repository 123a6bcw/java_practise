package ru.hse.kr3;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class MD5HashFilesTest {

    @Test
    void oneFileTest() throws InterruptedException, ExecutionException, IOException {
        testik("testFiles/justFile.txt");
    }

    @Test
    void directoryWithOneFileTest() throws InterruptedException, ExecutionException, IOException {
        testik("testFiles/OneFileInsidePapka/");
    }

    @Test
    void directoryWithManyFilesTest() throws InterruptedException, ExecutionException, IOException {
        testik("testFiles/allopapka");
    }

    /**
     * Template for testing a file.
     */
    private void testik(@NotNull String filename) throws InterruptedException, ExecutionException, IOException {
        var outputStream = new ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outputStream));

        String[] argc = {filename};
        MD5HashFiles.main(argc);

        var scanner = new Scanner(new ByteArrayInputStream(outputStream.toByteArray()));
        String md51 = scanner.nextLine();
        String md52 = scanner.nextLine();
        assertEquals(md51, md52);

        String time1 = scanner.nextLine();
        String time2 = scanner.nextLine();

        assertNotEquals("", time1);
        assertNotEquals("", time2);

        assertNotNull(time1);
        assertNotNull(time2);

        assertFalse(scanner.hasNextLine());
    }
}