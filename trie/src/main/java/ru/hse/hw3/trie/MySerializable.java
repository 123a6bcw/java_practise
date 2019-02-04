package ru.hse.hw3.trie;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface MySerializable {
    /**
     * Transform object into byte sequence and write it to output stream
     */
    void serialize(OutputStream out) throws IOException;

    /**
     * Change data in object to the data read from InputStream
     */
    void deserialize(InputStream in) throws IOException;
}
