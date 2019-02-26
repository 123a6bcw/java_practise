package ru.hse.hw4.database;

import com.mongodb.MongoClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

/**
 *
 */
public class PhonebookDataBase {

    /**
     *
     */
    public static void main(String[] argc) {
        //Hides mongoDB logs from console (too many trash).
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);

        String dataBaseName = null;
        if (argc.length != 0) {
            dataBaseName = argc[0];
        } else {
            dataBaseName = "mainDataBase";
        }

        //Google says I'm not suppose to explicitly close MongoClient, but does not work otherwise.
        try (var inputScanner = new Scanner(System.in);
             var mongoClient = new MongoClient()) {

            final var morphia = new Morphia();
            morphia.mapPackage("ru.hse.hw4.database");

            final Datastore datastore = morphia.createDatastore(mongoClient, dataBaseName);

            System.out.println("\nWrite help to get help");

            boolean stopInteraction = false; //
            while (!stopInteraction) {
                if (inputScanner.hasNextLine()) {
                    var commandScanner = new Scanner(inputScanner.nextLine());
                    switch (commandScanner.next()) {
                        case ("help"):
                            printHelp();
                            break;
                        case ("exit"):
                            stopInteraction = true;
                            break;
                        case ("addRecord"):
                            addRecord(commandScanner, datastore);
                            break;
                        case ("findPhones"):
                            findPhones(commandScanner, datastore);
                            break;
                        case ("findNames"):
                            findNames(commandScanner, datastore);
                            break;
                        case ("deleteRecord"):
                            deleteRecord(commandScanner, datastore);
                            break;
                        case ("changeName"):
                            changeName(commandScanner, datastore);
                            break;
                        case ("changePhone"):
                            changePhone(commandScanner, datastore);
                            break;
                        case ("printAll"):
                            printAll(datastore);
                            break;
                    }
                }
            }
        }
    }

    /**
     *Error:(10, 30) java: package ch.qos.logback.classic does not exist
     */
    private static void printHelp() {
        System.out.println("\nPlease use one of the following commands:\n" +
                "exit                                          exit the program\n" +
                "addRecord name phoneNumber                    adds new record with given name and phone number\n" +
                "findPhones {-byName name}                     finds all phones by given parameter\n" +
                "findNames  {-byPhone phoneNumber}             finds all names by given parameter\n" +
                "deleteRecord name phoneNumber                 deletes given record from the base\n" +
                "changeName name phoneNumber newName           changes name to newName for given record\n" +
                "changePhone name phoneNumber newPhoneNumber   changes phoneNumber to newPhoneNumber for given record\n" +
                "printAll                                      prints all record in the base\n");
    }

    /**
     *
     */
    private static void addRecord(Scanner commandScanner, Datastore datastore) {
        String[] parameters = getParameters(commandScanner, 2);
        if (parameters == null) {
            System.out.println("name or phone number is missing. No record has been added.\n");
            return;
        }

        String name = parameters[0], phone = parameters[1];

        datastore.save(new DataRecord(name, phone));
        System.out.println("Ok! Record " + name + " " + phone + " has been added\n");
    }

    /**
     *
     */
    private static void findPhones(Scanner commandScanner, Datastore datastore) {
        String key = getParameter(commandScanner);
        if (key == null) {
            System.out.println("No key specified for search\n");
            return;
        }

        switch (key) {
            case ("-byName"):
                String name = getParameter(commandScanner);
                if (name == null) {
                    System.out.println("no name specified for -byName search\n");
                    break;
                }

                List<DataRecord> records = datastore.find(DataRecord.class).field("name").equal(name).asList();
                if (records.size() == 0) {
                    System.out.println("no records with given name\n");
                } else {
                    for (var iterator = records.iterator(); iterator.hasNext();) {
                        var record = iterator.next();
                        System.out.print(record.getPhone());
                        if (iterator.hasNext()) {
                            System.out.println(", ");
                        }
                    }
                    System.out.print("\n\n");
                }
                break;
            default:
                System.out.println("unknown key\n");
        }
    }

    /**
     *
     */
    private static void findNames(Scanner commandScanner, Datastore datastore) {
        String findNamesKey = getParameter(commandScanner);
        if (findNamesKey == null) {
            System.out.println("key is missing\n");
            return;
        }

        switch (findNamesKey) {
            case ("-byPhone"):
                String phone = getParameter(commandScanner);
                if (phone == null) {
                    System.out.println("no phone number specified for -byPhone search\n");
                }

                List<DataRecord> records = datastore.find(DataRecord.class).field("phone").equal(phone).asList();
                if (records.size() == 0) {
                    System.out.println("no records with given phone\n");
                } else {
                    for (var iterator = records.iterator(); iterator.hasNext();) {
                        var record = iterator.next();
                        System.out.print(record.getName());
                        if (iterator.hasNext()) {
                            System.out.print(", ");
                        }
                    }
                    System.out.print("\n\n");
                }
                break;
            default:
                System.out.println("unknown key\n");
        }
    }

    /**
     *
     */
    private static void deleteRecord(Scanner commandScanner, Datastore datastore) {
        String[] parameters = getParameters(commandScanner, 3);
        if (parameters == null) {
            System.out.println("name or phone not specified, no record has been deleted\n");
            return;
        }

        String deleteName = parameters[0], deletePhone = parameters[1];

        datastore.delete(new DataRecord(deleteName, deletePhone));
        System.out.println("Ok! Record " + deleteName + " " + deletePhone + " has been deleted\n");
    }

    /**
     *
     */
    private static void changeName(Scanner commandScanner, Datastore datastore) {
        String[] parameters = getParameters(commandScanner, 3);
        if (parameters == null) {
            System.out.println("name, phone or newName not specified, no record has been changed\n");
            return;
        }

        String name = parameters[0], phone = parameters[1], newName = parameters[2];

        Query<DataRecord> record = getRecordFromDatastore(datastore, name, phone);
        if (record == null) {
            System.out.println("No record with given name and phone\n");
        } else {
            datastore.findAndModify(record,
                    datastore.createUpdateOperations(DataRecord.class).set("name", newName));
            System.out.println("Ok! " + name + " has been changed to " + newName + "\n");
        }
    }

    /**
     *
     */
    private static void changePhone(Scanner commandScanner, Datastore datastore) {
        String[] parameters = getParameters(commandScanner, 3);
        if (parameters == null) {
            System.out.println("name, phone or newPhone was not specified, no record has been changed\n");
            return;
        }

        String name = parameters[0], phone = parameters[1], newPhone = parameters[2];

        Query<DataRecord> record = getRecordFromDatastore(datastore, name, phone);
        if (record == null) {
            System.out.print("No record with given name and phone\n");
        } else {
            datastore.findAndModify(record,
                    datastore.createUpdateOperations(DataRecord.class).set("phone", newPhone));
            System.out.println("Ok! " + phone + " has been changed to " + newPhone + "\n");
        }
    }

    /**
     *
     */
    private static void printAll(Datastore datastore) {
        for (var iterator = datastore.createQuery(DataRecord.class).iterator(); iterator.hasNext();) {
            var record = iterator.next();
            System.out.print(record.getName() + record.getPhone());

            if (iterator.hasNext()) {
                System.out.print(", ");
            }
        }
        System.out.print("\n\n");
    }

    /**
     *
     */
    @Nullable
    private static Query<DataRecord> getRecordFromDatastore(Datastore datastore, String name, String phone) {
        var result = datastore.find(DataRecord.class).field("name").equal(name).field("phone").equal(phone);
        if (result.get() == null) {
            return null;
        }

        return result;
    }
    /**
     *
     */
    @Nullable
    private static String getParameter(@NotNull Scanner scanner) {
        if (scanner.hasNext()) {
            return scanner.next();
        } else {
            return null;
        }
    }

    /**
     *
     */
    @Nullable
    private static String[] getParameters(@NotNull Scanner scanner, int numberOfParameters) {
        var result = new String[numberOfParameters];
        for (int i = 0; i < numberOfParameters; i++) {
            if (scanner.hasNext()) {
                result[i] = scanner.next();
            } else {
                return null;
            }
        }

        return result;
    }
}

