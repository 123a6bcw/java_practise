package ru.hse.hw4.database;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.nio.ByteBuffer;
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
     * Phone id
     */
    @Id
    private ObjectId id;

    /**
     * Phone number stored.
     */
    private String phone;

    /**
     * Id of owners of this phone number.
     */
    private Set<ObjectId> owners;

    public DataPhone() {
    }

    public DataPhone(String phone) {
        this.phone = phone;
        id = new ObjectId();
        owners = new HashSet<>();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean addOwner(DataPerson owner) {
        return owners.add(owner.getId());
    }

    /*
    public DataPerson getOwner(String name) {
        return owners.stream().filter(a -> a.getName().equals(name)).findAny().orElse(null);
    }
    */

    public boolean removeOwner(DataPerson owner) {
        return owners.remove(owner.getId());
    }

    public Set<ObjectId> getOwners() {
        return owners;
    }

    public void setOwners(Set<ObjectId> owners) {
        this.owners = owners;
    }

    public boolean changeOwner(DataPerson oldOwner, DataPerson newOwner) {
        if (!owners.contains(oldOwner.getId()) || owners.contains(newOwner.getId())) {
            return false;
        }

        owners.remove(oldOwner.getId());
        owners.add(newOwner.getId());
        return true;
    }
}
