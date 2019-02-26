package ru.hse.hw4.database;

import org.mongodb.morphia.annotations.*;

/**
 *
 */
@Entity
public class DataRecord {
    @Id
    private int id;
    private String name;
    private String phone;

    public DataRecord() {
        name = null;
        phone = null;
    }

    public DataRecord(String name, String record) {
        this.name = name;
        this.phone = record;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
