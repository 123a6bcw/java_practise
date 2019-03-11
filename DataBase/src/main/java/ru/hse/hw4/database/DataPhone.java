package ru.hse.hw4.database;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<DataPerson> owners;

    public DataPhone(String phone) {
        this.phone = phone;
        this.id = phone.hashCode();
        this.owners = new HashSet<>();
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

    public boolean addOwner(DataPerson owner) {
        return owners.add(owner);
    }

    public boolean removeOwner(DataPerson owner) {
        return owners.remove(owner);
    }

    public Set<DataPerson> getOwners() {
        return getOwners();
    }

    public boolean changeOwner(DataPerson oldOwner, DataPerson newOwner) {
        if (!owners.contains(oldOwner) || owners.contains(newOwner)) {
            return false;
        }

        owners.remove(oldOwner);
        owners.add(newOwner);
        return true;
    }
}
