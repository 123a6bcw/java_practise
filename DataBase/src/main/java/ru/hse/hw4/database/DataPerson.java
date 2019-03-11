package ru.hse.hw4.database;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @Id
    private String name;

    /**
     * Phones of this person
     */
    @Reference
    private Set<DataPhone> phones;

    public DataPerson(String name) {
        this.name = name;
        this.id = name.hashCode();
        this.phones = new HashSet<>();
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

    public boolean addPhone(DataPhone phone) {
        return phones.add(phone);
    }

    public boolean removePhone(DataPhone phone) {
        return phones.remove(phone);
    }

    public Set<DataPhone> getPhones() {
        return phones;
    }

    public boolean changePhone(DataPhone oldPhone, DataPhone newPhone) {
        if (!phones.contains(oldPhone) || phones.contains(newPhone)) {
            return false;
        }

        phones.remove(oldPhone);
        phones.add(newPhone);
        return true;
    }
}