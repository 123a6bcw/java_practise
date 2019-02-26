package ru.hse.hw4.database;

import com.mongodb.MongoClient;
import org.junit.jupiter.api.*;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class PhonebookDataBaseTest {
    private MongoClient mongoClient = new MongoClient();
    private final Morphia morphia = new Morphia();

    /**
     * Stream to which will be redirected System.out in order to read it.
     */
    private ByteArrayOutputStream outputStream;

    /**
     * Arguments for PhoneBookDataBase.main(String[]);
     */
    private String[] argc = {"testBase"};

    /**
     * The very first line in any database interaction.
     */
    private String intro = "\nWrite help to get help\n\n";

    @BeforeEach
    void setUp() {
        morphia.mapPackage("ru.hse.hw4.database");
        morphia.map(DataRecord.class);
        final Datastore datastore = morphia.createDatastore(mongoClient, "testBase");

        outputStream = new ByteArrayOutputStream(1000);
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void mainHelp() {
        String commands = "help\nexit\n";
        setCommands(commands);

        PhonebookDataBase.main(argc);

        String result = getResult();
        assertEquals(intro + "Please use one of the following commands:\n" +
                "exit                                          exit the program\n" +
                "addRecord name phoneNumber                    adds new record with given name and phone number\n" +
                "findPhones {-byName name}                     finds all phones by given parameter\n" +
                "findNames  {-byPhone phoneNumber}             finds all names by given parameter\n" +
                "deleteRecord name phoneNumber                 deletes given record from the base\n" +
                "changeName name phoneNumber newName           changes name to newName for given record\n" +
                "changePhone name phoneNumber newPhoneNumber   changes phoneNumber to newPhoneNumber for given record\n" +
                "printAll                                      prints all record in the base\n\n" +
                "Please use only digits, '-', '(' and ')' when adds phone numbers (this will be checked, so don't worry)\n" +
                "Don't use whitespaces in names or phone numbers!\n" +
                "Please note that database does not check your phone number for correctness! (Too lazy)\n\n", result);
    }

    /**
     * Prints given commands to System.in in order for database to read those commands.
     */
    private void setCommands(String commands) {
        var byteStream = new ByteArrayOutputStream();
        var printStream = new PrintStream(byteStream);
        printStream.print(commands);
        System.setIn(new ByteArrayInputStream(byteStream.toByteArray()));
    }

    /**
     * Returns all content of System.out (which was set to outputStream).
     */
    private String getResult() {
        var scanner = new Scanner(new ByteArrayInputStream(outputStream.toByteArray()));
        var result = new StringBuilder();
        while (scanner.hasNextLine()) {
            result.append(scanner.nextLine()).append("\n");
        }
        return result.toString();
    }

    @AfterEach
    void tearDown() throws IOException {
        outputStream.close();
    }
}