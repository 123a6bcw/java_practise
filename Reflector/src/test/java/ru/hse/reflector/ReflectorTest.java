package ru.hse.reflector;

import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ReflectorTest {

    @Test
    void mainThing() throws IOException {
        Reflector.printStructure(Reflector.closs.class);
    }
}