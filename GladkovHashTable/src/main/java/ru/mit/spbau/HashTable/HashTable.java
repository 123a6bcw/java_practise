package ru.mit.spbau.HashTable;

/**
 * Copyright (c) 2017 Gladkov Alexander
 */

public class HashTable {
    private int capacity;
    /**
     * Size of the array in table. Increases if needed.
     */
    private int size;
    /**
     * Number of objects in table
     */
    private List data[];

    /**
     * Just some tests to make sure it works correctly
     */
    public static void main(String args[]) {
        HashTable a = new HashTable();
        System.out.println(a.size() + " 0");
        System.out.println(a.contains("sasha") + " false");
        System.out.println(a.put("sasha", "clever") + " null");
        System.out.println(a.contains("sasha") + " true");
        System.out.println(a.put("java", "rules") + " null");
        System.out.println(a.contains("java") + " true");
        System.out.println(a.put("sasha", "smart") + " clever");
        System.out.println(a.contains("sasha") + " true");
        System.out.println(a.size() + " 2");
        System.out.println(a.get("sasha") + " smart");
        System.out.println(a.get("java") + " rules");
        System.out.println(a.remove("sasha") + " smart");
        System.out.println(a.contains("sasha") + " false");
        System.out.println(a.get("sasha") + " null");
        System.out.println(a.size() + " 1");
        a.clear();
        System.out.println(a.size() + " 0");
        System.out.println(a.get("java") + " null");
        System.out.println(a.contains("java") + " false");
        System.out.println(a.remove("Dima") + " null");
        System.out.println(a.size() + " 0");
    }

    HashTable() {
        capacity = 8;
        size = 0;
        data = new List[capacity];

        for (int i = 0; i < capacity; i++) {
            data[i] = new List();
        }
    }

    public int size() {
        return size;
    }

    /**
     * Check if table contains object with such key.
     */
    public boolean contains(String key) {
        Hashator accessor = new Hashator(key);
        return data[accessor.dataIndex].contains(accessor.hashedKey);
    }

    /**
     * Returns object from table with such key
     */
    public String get(String key) {
        Hashator accessor = new Hashator(key);
        return data[accessor.dataIndex].get(accessor.hashedKey);
    }

    /**
     * Puts an object into table with such key
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
     * removes an object from table with such key
     */
    public String remove(String key) {
        Hashator accessor = new Hashator(key);
        String returnValue = data[accessor.dataIndex].remove(accessor.hashedKey);
        if (returnValue != null) {
            size--;
            /*
            null means were wasn't an object with such key, so we hadn't deleted anything,
            otherwise we had.
             */
        }

        return returnValue;
    }

    /**
     * deletes all objects from table
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
            capacity *= 2; //need to be here for Hashator() works correctly

            List newData[] = new List[capacity];
            for (int i = 0; i < capacity; i++) {
                newData[i] = new List();
            }

            for (int i = 0; 2 * i < capacity; i++) {
                for (Node x : data[i]) {
                    Hashator accessor = new Hashator(x.key);
                    newData[accessor.dataIndex].push(x); //push works with O(1)
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
        int hashedKey;
        int dataIndex;
        static final int mod = 1000000007;
        static final int base = 31;

        Hashator(String key) {
            hashedKey = 0;
            for (int i = 0; i < key.length(); i++) {
                hashedKey = (hashedKey * base + key.charAt(i)) % mod;
            }

            dataIndex = hashedKey % capacity;
        }

        /**
         * needs than we get object from an old HashTable and which hashedKey we already know
         */
        Hashator(int key) {
            hashedKey = key;
            dataIndex = hashedKey % capacity;
        }
    }
}
