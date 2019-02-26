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
    private String intro = "\nWrite help to get help\n";

    @BeforeEach
    void setUp() {
        /*
        Clears datastore before every test so where will be no trash from previous tests.
        Everywhere I'm accessing only byte array streams so I don't bother closing it.
         */
        setCommands("clear\nexit\n");
        System.setOut(new PrintStream(new ByteArrayOutputStream(1000)));
        PhonebookDataBase.main(argc);

        outputStream = new ByteArrayOutputStream(2000);
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void help() {
        setCommands("help\nexit\n");

        PhonebookDataBase.main(argc);

        String result = getResult();
        assertEquals(intro + "\nPlease use one of the following commands:\n" +
                "exit                                          exit the program.\n" +
                "addRecord name phoneNumber                    adds new record with given name and phone number.\n" +
                "findPhones {-byName name}                     finds all phones by given parameter.\n" +
                "findNames  {-byPhone phoneNumber}             finds all names by given parameter.\n" +
                "deleteRecord name phoneNumber                 deletes given record from the base.\n" +
                "changeName name phoneNumber newName           changes name to newName for given record.\n" +
                "changePhone name phoneNumber newPhoneNumber   changes phoneNumber to newPhoneNumber for given record.\n" +
                "printAll                                      prints all record in the base.\n" +
                "clear                                         deletes all records from the base. No backup.\n\n" +
                "Please use only digits, '-', '(' and ')' when adds phone numbers (this will be checked, so don't worry).\n" +
                "Don't use whitespaces in names or phone numbers!\n" +
                "Please note that database does not check your phone number for correctness! (Too lazy)\n\n", result);
    }

    @Test
    void addRecord() {
        setCommands("addRecord sasha 8-800-555-35-35\n" +
                "printAll\n"+
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                addRecordMessage("sasha", "8-800-555-35-35") +
                "sasha 8-800-555-35-35\n\n", result);
    }

    @Test
    void addRecordMissingArgumentsDoesNotAddsRecord() {
        setCommands("addRecord sasha\n" +
                "addRecord\n" +
                "printAll\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "name or phone number is missing. No record has been added.\n\n" +
                "name or phone number is missing. No record has been added.\n\n" +
                "no records in database\n\n", result);
    }

    @Test
    void addRecordWrongNumberDoesNotAddsRecord() {
        setCommands("addRecord sasha sasha\n" +
                "addRecord sasha 8-800()()11a\n" +
                "printAll\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "incorrect symbols in phone number! Please use only whitespaces, digits, '-', '(' and ')'\n\n" +
                "incorrect symbols in phone number! Please use only whitespaces, digits, '-', '(' and ')'\n\n" +
                "no records in database\n\n", result);
    }

    @Test
    void addRecordDuplicatedRecordDoesNotAdds() {
        setCommands("addRecord sasha 800\n" +
                "addRecord sasha 800\n" +
                "printAll\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                addRecordMessage("sasha", "800") +
                "Given record already exists in data base!\n\n" +
                "sasha 800\n\n", result);
    }

    @Test
    void findPhonesWrongArguments() {
        setCommands("findPhones\n" +
                "findPhones -byMyBlade\n" +
                "findPhones -byName\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "No key specified for search\n\n" +
                "unknown key\n\n" +
                "no name specified for -byName search\n\n", result);
    }

    @Test
    void findPhonesNotFound() {
        setCommands("findPhones -byName s\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "no records with given name\n\n", result);
    }

    @Test
    void findPhonesByName() {
        setCommands("addRecord sasha 699\n" +
                "addRecord sasha 700\n" +
                "addRecord dima 699\n" +
                "findPhones -byName sasha\n" +
                "findPhones -byName dima\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                addRecordMessage("sasha", "699") +
                addRecordMessage("sasha", "700") +
                addRecordMessage("dima", "699") +
                "699, 700\n\n" +
                "699\n\n", result);
    }

    @Test
    void findNamesWrongArguments() {
        setCommands("findNames\n" +
                "findNames -byMyBlade\n" +
                "findNames -byPhone\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "No key specified for search\n\n" +
                "unknown key\n\n" +
                "no phone number specified for -byPhone search\n\n", result);
    }

    @Test
    void findNamesNotFound() {
        setCommands("findNames -byPhone 322\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "no records with given phone\n\n", result);
    }

    @Test
    void findNamesByName() {
        setCommands("addRecord sasha 699\n" +
                "addRecord sasha 700\n" +
                "addRecord dima 699\n" +
                "findPhones -byName sasha\n" +
                "findPhones -byName dima\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                addRecordMessage("sasha", "699") +
                addRecordMessage("sasha", "700") +
                addRecordMessage("dima", "699") +
                "699, 700\n\n" +
                "699\n\n", result);
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

    /**
     * Returns message after correct adding record with given name and phone.
     */
    private String addRecordMessage(String name, String phone) {
        return "Ok! Record " + name + " " + phone + " has been added\n\n";
    }

    @AfterEach
    void tearDown() throws IOException {
        outputStream.close();
    }
}