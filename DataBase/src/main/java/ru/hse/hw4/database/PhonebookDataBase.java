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
import java.util.Scanner;

/**
 * A simple phonebook database with console interface.
 */
public class PhonebookDataBase {

    /**
     * Runs infinitive loop which reads user's command and updates database.
     * If args length is more than 0, the first argument is the name of the database. Otherwise it's name is "mainDataBase".
     */
    public static void main(String[] argc) {
        //Hides mongoDB logs from console (too many trash).
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);

        String dataBaseName;
        if (argc.length != 0) {
            dataBaseName = argc[0];
        } else {
            dataBaseName = "mainDataBase";
        }

        //Google says I'm not suppose to explicitly close MongoClient, but it does not work otherwise.
        try (var inputScanner = new Scanner(System.in);
             var mongoClient = new MongoClient()) {

            final var morphia = new Morphia();
            morphia.mapPackage("ru.hse.hw4.database");
            morphia.map(DataRecord.class);

            final Datastore datastore = morphia.createDatastore(mongoClient, dataBaseName);

            //See printHelp() to read help!
            System.out.println("\nWrite help to get help.");

            boolean stopInteraction = false;
            while (!stopInteraction) {
                if (inputScanner.hasNextLine()) {
                    var commandScanner = new Scanner(inputScanner.nextLine());
                    if (!commandScanner.hasNext()) {
                        continue;
                    }

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
                        case ("clear"):
                            clear(datastore, inputScanner);
                            break;
                        default:
                            System.out.println("Unknown command! Please read help by printing help.\n");
                            break;
                    }
                }
            }
        }
    }

    /**
     * Deletes all records from datastore.
     */
    private static void clear(@NotNull Datastore datastore, @NotNull Scanner inputScanner) {
        System.out.println("Are you sure?? That will delete everything!\nPlease write yes or no.");
        while (true) {
            if (inputScanner.hasNextLine()) {
                String line = inputScanner.nextLine();
                if (line.equals("yes") || line.equals("Yes") || line.equals("Y") || line.equals("y")) {
                    datastore.delete(datastore.createQuery(DataRecord.class));
                    System.out.println("Ok! Everything was deleted. It's all gone.\n");
                    break;
                } else if (line.equals("no") || line.equals("No") || line.equals("n") || line.equals("N")) {
                    System.out.println("Ok! Nothing was deleted!\n");
                    break;
                } else {
                    System.out.println("Please write yes if you want to clear database and no otherwise.");
                }
            }
        }
    }

    /**
     * Prints info about commands supported by this database.
     */
    private static void printHelp() {
        System.out.println("\nPlease use one of the following commands:\n"
                + "exit                                          Exit the program.\n"
                + "addRecord name phoneNumber                    Adds new record with given name and phone number.\n"
                + "findPhones {-byName name}                     Finds all phones by given parameter.\n"
                + "findNames  {-byPhone phoneNumber}             Finds all names by given parameter.\n"
                + "deleteRecord name phoneNumber                 Deletes given record from the base.\n"
                + "changeName name phoneNumber newName           Changes name to newName for given record.\n"
                + "changePhone name phoneNumber newPhoneNumber   Changes phoneNumber to newPhoneNumber for given record.\n"
                + "printAll                                      Prints all records in the base.\n"
                + "clear                                         Deletes all records from the base. No backup.\n\n"
                + "Please use only digits, '-', '(' and ')' when adds phone numbers (this will be checked, so don't worry).\n"
                + "Don't use whitespaces in names or phone numbers!\n"
                + "Please note that database does not check your phone number for correctness! (Too lazy)\n");
    }

    /**
     * Reads name and phoneNumber from System.in. Adds corresponding record to the database.
     * Mostly does not make checks on correctness of the phone number. Only check if it uses only digits, whitespaces, '-', '(' and ')'.
     */
    private static void addRecord(@NotNull Scanner commandScanner, @NotNull Datastore datastore) {
        String[] parameters = getParameters(commandScanner, 2);
        if (parameters == null) {
            System.out.println("Name or phone number is missing. No record has been added.\n");
            return;
        }

        String name = parameters[0], phone = parameters[1];
        if (wrongPhone(phone)) {
            return;
        }

        Query<DataRecord> record = getRecordFromDatastore(datastore, name, phone);
        if (record != null) {
            System.out.println("Given record already exists in data base!\n");
            return;
        }

        datastore.save(new DataRecord(name, phone));
        System.out.println("Ok! Record " + name + " " + phone + " has been added.\n");
    }

    /**
     * Prints to System.out all phones found by specific filter.
     */
    private static void findPhones(@NotNull Scanner commandScanner, @NotNull Datastore datastore) {
        String key = getParameter(commandScanner);
        if (key == null) {
            System.out.println("No key specified for search.\n");
            return;
        }

        /*
        IDEA tells me there is too few cases and I probable should change switch to if statement.
        But the point is, where COULD be more cases, so I want to leave it as it is.
         */
        switch (key) {
            case ("-byName"):
                String name = getParameter(commandScanner);
                if (name == null) {
                    System.out.println("No name specified for -byName search.\n");
                    break;
                }

                List<DataRecord> records = datastore.find(DataRecord.class).field("name").equal(name).asList();
                if (records.size() == 0) {
                    System.out.println("No records with given name.\n");
                } else {
                    for (var iterator = records.iterator(); iterator.hasNext();) {
                        var record = iterator.next();
                        System.out.print(record.getPhone());
                        if (iterator.hasNext()) {
                            System.out.print(", ");
                        }
                    }
                    System.out.print("\n\n");
                }
                break;
            default:
                System.out.println("Unknown key.\n");
        }
    }

    /**
     * Prints to System.out all names found by specific filter.
     */
    private static void findNames(@NotNull Scanner commandScanner, @NotNull Datastore datastore) {
        String findNamesKey = getParameter(commandScanner);
        if (findNamesKey == null) {
            System.out.println("No key specified for search.\n");
            return;
        }

        switch (findNamesKey) {
            case ("-byPhone"):
                String phone = getParameter(commandScanner);
                if (phone == null) {
                    System.out.println("No phone number specified for -byPhone search.\n");
                    break;
                }

                List<DataRecord> records = datastore.find(DataRecord.class).field("phone").equal(phone).asList();
                if (records.size() == 0) {
                    System.out.println("No records with given phone.\n");
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
                System.out.println("Unknown key.\n");
        }
    }

    /**
     * Reads name and phoneNumber from System.in , deletes corresponding record from the database (if it exists).
     */
    private static void deleteRecord(@NotNull Scanner commandScanner, @NotNull Datastore datastore) {
        String[] parameters = getParameters(commandScanner, 2);
        if (parameters == null) {
            System.out.println("Name or phone not specified, no record has been deleted.\n");
            return;
        }

        String deleteName = parameters[0];
        String deletePhone = parameters[1];

        Query<DataRecord> record = getRecordFromDatastore(datastore, deleteName, deletePhone);
        if (record == null) {
            System.out.println("No given record found in database.\n");
        } else {
            datastore.findAndDelete(record);
            System.out.println("Ok! Record " + deleteName + " " + deletePhone + " has been deleted.\n");
        }
    }

    /**
     * Reads name, phoneNumber and newName from System.in , changes name to newName in corresponding record if it exists.
     */
    private static void changeName(@NotNull Scanner commandScanner, @NotNull Datastore datastore) {
        String[] parameters = getParameters(commandScanner, 3);
        if (parameters == null) {
            System.out.println("Name, phone or newName not specified, no record has been changed.\n");
            return;
        }

        String name = parameters[0];
        String phone = parameters[1];
        String newName = parameters[2];

        Query<DataRecord> record = getRecordFromDatastore(datastore, name, phone);
        if (record == null) {
            System.out.println("No record with given name and phone.\n");
        } else if (getRecordFromDatastore(datastore, newName, phone) != null) {
            System.out.println("Nothing has been changed! Record with new name and given phone already exists!\n");
        } else {
            datastore.findAndModify(record,
                    datastore.createUpdateOperations(DataRecord.class).set("name", newName));
            System.out.println("Ok! " + name + " has been changed to " + newName + ".\n");
        }
    }

    /**
     * Reads name, phoneNumber and newPhoneNumber from System.in , changes phoneNumber to newPhoneNumber in corresponding
     * record if it exists.
     */
    private static void changePhone(@NotNull Scanner commandScanner, @NotNull Datastore datastore) {
        String[] parameters = getParameters(commandScanner, 3);
        if (parameters == null) {
            System.out.println("Name, phone or newPhone not specified, no record has been changed.\n");
            return;
        }

        String name = parameters[0];
        String phone = parameters[1];
        String newPhone = parameters[2];
        if (wrongPhone(newPhone)) {
            return;
        }

        Query<DataRecord> record = getRecordFromDatastore(datastore, name, phone);
        if (record == null) {
            System.out.println("No record with given name and phone.\n");
        } else if (getRecordFromDatastore(datastore, name, newPhone) != null) {
            System.out.println("Nothing has been changed! Record with given name and new phone already exists!\n");
        } else {
            datastore.findAndModify(record,
                    datastore.createUpdateOperations(DataRecord.class).set("phone", newPhone));
            System.out.println("Ok! " + phone + " has been changed to " + newPhone + ".\n");
        }
    }

    /**
     * Prints to System.out all records in database.
     */
    private static void printAll(@NotNull Datastore datastore) {
        List<DataRecord> records = datastore.createQuery(DataRecord.class).asList();
        if (records.size() == 0) {
            System.out.println("No records in database.\n");
            return;
        }

        for (var iterator = records.iterator(); iterator.hasNext();) {
            var record = iterator.next();
            System.out.print(record.getName() + " " + record.getPhone());

            if (iterator.hasNext()) {
                System.out.print(", ");
            }
        }
        System.out.print("\n\n");
    }

    /**
     * Returns false if given phone is correct phone number (contains only digits, '-', '(' and ')').
     * If phone is not correct, writes corresponding message to System.out.
     */
    private static boolean wrongPhone(@NotNull String phone) {
        for (int i = 0; i < phone.length(); i++) {
            if (phone.charAt(i) != '-' && phone.charAt(i) != '(' && phone.charAt(i) != ')' && (phone.charAt(i) < '0' || phone.charAt(i) > '9')) {
                System.out.println("Incorrect symbols in phone number! Please use only whitespaces, digits, '-', '(' and ')'.\n");
                return true;
            }
        }
        return false;
    }

    /**
     * Returns Query with exactly one DataRecord --- one that has the same name and phoneNumber as given ones.
     * Returns null if there is no such record.
     */
    @Nullable
    private static Query<DataRecord> getRecordFromDatastore(@NotNull Datastore datastore, @NotNull String name, @NotNull String phone) {
        var result = datastore.find(DataRecord.class).field("name").equal(name).field("phone").equal(phone);
        if (result.get() == null) {
            return null;
        }

        return result;
    }

    /**
     * Gets one parameter (token seperated by any delimeters) from the scanner.
     * Returns null if there is no next token in scanner.
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
     * Gets numberOfParameters parameters (tokens seperated by any delimeters) from the scanner.
     * Returns null if there is no enough tokens in scanner.
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

