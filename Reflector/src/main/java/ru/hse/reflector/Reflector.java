package ru.hse.reflector;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Reflector {
    public void printStructure(Class<?> someClass) throws IOException {
        var file = new File("sdeemeedomeClass.java");
        if (!file.canWrite()) {
            throw new RuntimeException("Can't read file someClass.java");
        }

        var writer = new FileWriter(file);


    }
}
