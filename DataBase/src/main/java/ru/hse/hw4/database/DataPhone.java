package ru.hse.hw4.database;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing phones in phonebook.
 */
@Entity
public class DataPhone {

    /**
     * Id is phone's hashcode.
     */
    @Id
    private int id;

    /**
     * Phone number stored.
     */
    private String phone;

    /**
     * Owners of this phone number.
     */
    @Reference
    private List<DataPerson> owners;

    public DataPhone(String phone) {
        this.phone = phone;
        this.id = phone.hashCode();
        this.owners = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
