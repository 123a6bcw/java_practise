package ru.hse.hw4.database;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing people in phonebook.
 */
@Entity
public class DataPerson {

    /**
     * Id is hashcode of man's name.
     */
    @Id
    private int id;

    /**
     * Name of this man.
     */
    private String name;

    @Reference
    private List<DataPhone> phones;

    public DataPerson(String name) {
        this.name = name;
        this.id = name.hashCode();
        this.phones = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
