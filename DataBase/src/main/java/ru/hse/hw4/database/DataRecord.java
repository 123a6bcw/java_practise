package ru.hse.hw4.database;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

/**
 * Class for storing records in phonebook.
 */
@Entity
public class DataRecord {

    /**
     * Any record in database has unique id created by morphia.
     */
    @Id
    private ObjectId id;

    /**
     * Name of the man with this phone.
     */
    private String name;

    /**
     * Phone of the man with this phone.
     */
    private String phone;

    public DataRecord() {
        name = null;
        phone = null;
        id = new ObjectId();
    }

    public DataRecord(String name, String record) {
        this.name = name;
        this.phone = record;
        this.id = new ObjectId();
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
