package ru.hse.hashtable;

/**
 * Simple HashTable class.
 * Copyright (c) 2019 Gladkov Alexander
 */

public class HashTable {
    /**
     * Number of reserved lists in table.
     */
    private int capacity;

    /**
     * default capacity size when creating table.
     */
    private static final int defaultCapacity = 8;

    /**
     * Number of objects in the table.
     */
    private int size;

    /**
     * Array of lists with objects with size of capacity.
     */
    private List[] data;

    /**
     * Just some tests to make sure it works correctly.
     */
    public static void main(String[] args) {
    }

    /**
     * creates HashTable with given capacity, 0 size and array of 'capacity' empty Lists.
     */
    public HashTable(int capacity) {
        this.capacity = capacity;
        size = 0;
        data = new List[capacity];

        for (int i = 0; i < capacity; i++) {
            data[i] = new List();
        }
    }

    /**
     * creates table with default capacity.
     */
    public HashTable() {
        this(defaultCapacity);
    }

    /**
     * @return number of elements in hash table.
     */
    public int size() {
        return size;
    }

    /**
     * @param key object's key.
     * @return true if table contains object with given key, false otherwise.
     */
    public boolean contains(String key) {
        Hashator accessor = new Hashator(key);
        return data[accessor.dataIndex].contains(accessor.hashedKey);
    }

    /**
     * @param key object's key.
     * @return value of object with given key.
     */
    public String get(String key) {
        Hashator accessor = new Hashator(key);
        return data[accessor.dataIndex].get(accessor.hashedKey);
    }

    /**
     * @param key object's key
     * @param value object's value
     * Puts an object into table with given key if there is no object with the same key, otherwise overrides it's value
     * @return old value of object from table or null if there was no such one
     */
    public String put(String key, String value) {
        Hashator accessor = new Hashator(key);
        String returnValue = data[accessor.dataIndex].insert(accessor.hashedKey, value);
        if (returnValue == null) {
            size++;
            expandData();
            /*
            null means there wasn't object with such key,
            so we added a new object instead of overriding an old one
            */
        }

        return returnValue;
    }

    /**
     * @param key object's key
     * removes an object from the table with given key
     * @return value of deleted object or null if there was no such object
     */
    public String remove(String key) {
        Hashator accessor = new Hashator(key);
        String returnValue = data[accessor.dataIndex].remove(accessor.hashedKey);
        if (returnValue != null) {
            size--;
            /*
            null means were wasn't an object with such key, so we haven't deleted anything,
            otherwise we have.
             */
        }

        return returnValue;
    }

    /**
     * deletes all objects from table.
     */
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            data[i].clear();
        }
        size = 0;
    }

    /**
     * Checks if table contains too many objects, so starting to work slow.
     * If so, creates a 2 times larger table and moves all objects there,
     * also replacing all objects by getting hash divided by new capacity (rehashing).
     */
    private void expandData() {
        if (4 * size >= capacity) {
            capacity *= 2; //need to be here so Hashator() works correctly

            List[] newData = new List[capacity];
            for (int i = 0; i < capacity; i++) {
                newData[i] = new List();
            }

            for (int i = 0; 2 * i < capacity; i++) {
                for (Node x : data[i]) {
                    Hashator accessor = new Hashator(x.getKey());
                    newData[accessor.dataIndex].push(x); //push works in O(1)
                }
            }

            data = newData;
        }
    }

    /**
     * this class gets hash from key-string and makes index in data array for this hash
     * not static, cause it being used only inside this class and uses it's capacity.
     */
    private class Hashator {
        private int hashedKey;
        private int dataIndex;
        private static final int mod = 1000000007;
        private static final int base = 31;

        /**
         * mathematically correct mod.
         * @return a `mod` b.
         */
        private int safeMod(int a, int b) {
            a %= b;
            if (a < 0) {
                a += b;
            }

            return a;
        }

        private Hashator(String key) {
            hashedKey = 0;
            for (int i = 0; i < key.length(); i++) {
                hashedKey = (hashedKey * base + key.charAt(i)) % mod;
            }

            dataIndex = safeMod(hashedKey, capacity);
        }

        /**
         * needs than we get object from an old HashTable and which hashedKey we already know
         */
        private Hashator(int key) {
            hashedKey = key;
            dataIndex = safeMod(hashedKey, capacity);
        }
    }
}