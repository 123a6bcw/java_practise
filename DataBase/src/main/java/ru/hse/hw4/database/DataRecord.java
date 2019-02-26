package ru.hse.hw4.database;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

/**
 *
 */
@Entity
public class DataRecord {
    @Id
    private ObjectId id;

    private String name;
    private String phone;

    public DataRecord() {
        name = null;
        phone = null;
        id = new ObjectId();
    }

    public DataRecord(String name, String record) {
        this.name = name;
        this.phone = record;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
