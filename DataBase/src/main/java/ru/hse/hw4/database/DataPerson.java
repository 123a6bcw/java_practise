package ru.hse.hw4.database;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.annotations.*;

import java.nio.ByteBuffer;
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
     * Id of the object
     */
    @Id
    private ObjectId id;

    /**
     * Name of this man.
     */
    private String name;

    /**
     * Id of phones attached to this person
     */
    /*
    Короче я не нашёл решения лучше, чем сделать сет из айдишников.
    Можно было сделать

    @Reference
    Set<DataPhone> phones

    Но тогда при загрузке человека из базы данных также бы выгружались все его телефоны, что звучит как-то отстойно
    (если телефонов много, а нам нужен всего один из них, то выгружалось бы слишком много).

    Ну либо можно было сохранить в базу данных отдельный класс, показывающий связь между DataPerson и DataPhone, но это
    бы ничем не отличалось от предыдущего решения.

    А ничего более умного морфия не умеет.
     */
    private Set<ObjectId> phones;

    public DataPerson() {
    }

    public DataPerson(String name) {
        this.name = name;
        this.id = new ObjectId();
        phones = new HashSet<>();
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

    public boolean addPhone(DataPhone phone) {
        return phones.add(phone.getId());
    }

    public boolean removePhone(DataPhone phone) {
        return phones.remove(phone.getId());
    }

    public Set<ObjectId> getPhones() {
        return phones;
    }

    public void setPhones(Set<ObjectId> phones) {
        this.phones = phones;
    }

    public boolean changePhone(DataPhone oldPhone, DataPhone newPhone) {
        if (!phones.contains(oldPhone.getId()) || phones.contains(newPhone.getId())) {
            return false;
        }

        phones.remove(oldPhone.getId());
        phones.add(newPhone.getId());
        return true;
    }
}