package ru.hse.hashtable;

public class StringPair {
    private int key;
    private String value;

    StringPair(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
