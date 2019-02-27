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
    private String intro = "\nWrite help to get help.\n";

    @BeforeEach
    void setUp() {
        /*
        Clears datastore before every test so where will be no trash from previous tests.
        Everywhere I'm accessing only byte array streams so I don't bother closing it.
         */
        setCommands("clear\nyes\nexit\n");
        System.setOut(new PrintStream(new ByteArrayOutputStream(100)));
        PhonebookDataBase.main(argc);

        outputStream = new ByteArrayOutputStream(200);
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void help() {
        setCommands("help\nexit\n");

        PhonebookDataBase.main(argc);

        String result = getResult();
        assertEquals(intro + "\nPlease use one of the following commands:\n" +
                "exit                                          Exit the program.\n" +
                "addRecord name phoneNumber                    Adds new record with given name and phone number.\n" +
                "findPhones {-byName name}                     Finds all phones by given parameter.\n" +
                "findNames  {-byPhone phoneNumber}             Finds all names by given parameter.\n" +
                "deleteRecord name phoneNumber                 Deletes given record from the base.\n" +
                "changeName name phoneNumber newName           Changes name to newName for given record.\n" +
                "changePhone name phoneNumber newPhoneNumber   Changes phoneNumber to newPhoneNumber for given record.\n" +
                "printAll                                      Prints all records in the base.\n" +
                "clear                                         Deletes all records from the base. No backup.\n\n" +
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
                "Name or phone number is missing. No record has been added.\n\n" +
                "Name or phone number is missing. No record has been added.\n\n" +
                "No records in database.\n\n", result);
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
                "Incorrect symbols in phone number! Please use only whitespaces, digits, '-', '(' and ')'.\n\n" +
                "Incorrect symbols in phone number! Please use only whitespaces, digits, '-', '(' and ')'.\n\n" +
                "No records in database.\n\n", result);
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
                "No key specified for search.\n\n" +
                "Unknown key.\n\n" +
                "No name specified for -byName search.\n\n", result);
    }

    @Test
    void findPhonesNotFound() {
        setCommands("findPhones -byName s\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "No records with given name.\n\n", result);
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
                "No key specified for search.\n\n" +
                "Unknown key.\n\n" +
                "No phone number specified for -byPhone search.\n\n", result);
    }

    @Test
    void findNamesNotFound() {
        setCommands("findNames -byPhone 322\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "No records with given phone.\n\n", result);
    }

    @Test
    void findNamesByPhone() {
        setCommands("addRecord sasha 699\n" +
                "addRecord sasha 700\n" +
                "addRecord dima 699\n" +
                "findNames -byPhone 699\n" +
                "findNames -byPhone 700\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                addRecordMessage("sasha", "699") +
                addRecordMessage("sasha", "700") +
                addRecordMessage("dima", "699") +
                "sasha, dima\n\n" +
                "sasha\n\n", result);
    }

    @Test
    void deleteRecordWrongParameters() {
        setCommands("deleteRecord\n" +
                "deleteRecord sasha\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "Name or phone not specified, no record has been deleted.\n\n" +
                "Name or phone not specified, no record has been deleted.\n\n", result);
    }

    @Test
    void deleteNotExistingRecordDoesNothing() {
        setCommands("addRecord a 5\n" +
                "addRecord b 7\n" +
                "addRecord a 9\n" +
                "deleteRecord c 1\n" +
                "deleteRecord a 2\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                addRecordMessage("a", "5") +
                addRecordMessage("b", "7") +
                addRecordMessage("a", "9") +
                "No given record found in database.\n\n" +
                "No given record found in database.\n\n", result);
    }

    @Test
    void deleteRecordDeletesRecords() {
        setCommands("addRecord a 5\n" +
                "addRecord b 7\n" +
                "addRecord a 9\n" +
                "deleteRecord a 5\n" +
                "printAll\n" +
                "deleteRecord a 9\n" +
                "deleteRecord b 7\n" +
                "printAll\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                addRecordMessage("a", "5") +
                addRecordMessage("b", "7") +
                addRecordMessage("a", "9") +
                "Ok! Record a 5 has been deleted.\n\n" +
                "b 7, a 9\n\n" +
                "Ok! Record a 9 has been deleted.\n\n" +
                "Ok! Record b 7 has been deleted.\n\n" +
                "No records in database.\n\n", result);
    }

    @Test
    void changeNameWrongParameters() {
        setCommands("changeName\n" +
                "changeName s\n" +
                "changeName s 6\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "Name, phone or newName not specified, no record has been changed.\n\n" +
                "Name, phone or newName not specified, no record has been changed.\n\n" +
                "Name, phone or newName not specified, no record has been changed.\n\n", result);
    }

    @Test
    void changeNameNoRecord() {
        setCommands("changeName s 6 a\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "No record with given name and phone.\n\n", result);
    }

    @Test
    void changeNameToExistingRecord() {
        setCommands("addRecord a 6\n" +
                "addRecord b 6\n" +
                "changeName a 6 b\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                addRecordMessage("a", "6") +
                addRecordMessage("b", "6") +
                "Nothing has been changed! Record with new name and given phone already exists!\n\n", result);
    }

    @Test
    void changeNameChangesName() {
        setCommands("addRecord a 6\n" +
                "addRecord b 9\n" +
                "changeName a 6 b\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                addRecordMessage("a", "6") +
                addRecordMessage("b", "9") +
                "Ok! a has been changed to b.\n\n", result);
    }

    @Test
    void changePhoneWrongParameters() {
        setCommands("changePhone\n" +
                "changePhone s\n" +
                "changePhone s 6\n" +
                "changePhone s 6 s\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "Name, phone or newPhone not specified, no record has been changed.\n\n" +
                "Name, phone or newPhone not specified, no record has been changed.\n\n" +
                "Name, phone or newPhone not specified, no record has been changed.\n\n" +
                "Incorrect symbols in phone number! Please use only whitespaces, digits, '-', '(' and ')'.\n\n", result);
    }

    @Test
    void changePhoneNoRecord() {
        setCommands("changePhone s 6 8\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "No record with given name and phone.\n\n", result);
    }

    @Test
    void changePhoneToExistingRecord() {
        setCommands("addRecord a 6\n" +
                "addRecord a 9\n" +
                "changePhone a 6 9\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                addRecordMessage("a", "6") +
                addRecordMessage("a", "9") +
                "Nothing has been changed! Record with given name and new phone already exists!\n\n", result);
    }

    @Test
    void changePhoneChangesPhone() {
        setCommands("addRecord a 6\n" +
                "addRecord a 9\n" +
                "changePhone a 6 8\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                addRecordMessage("a", "6") +
                addRecordMessage("a", "9") +
                "Ok! 6 has been changed to 8.\n\n", result);
    }

    @Test
    void printAllEmptyDatabase() {
        setCommands("printAll\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "No records in database.\n\n", result);
    }

    @Test
    void printAllOneElement() {
        setCommands("addRecord s 5\n" +
                "printAll\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                addRecordMessage("s", "5") +
                "s 5\n\n", result);
    }

    @Test
    void printAllSeveralElements() {
        setCommands("addRecord s 5\n" +
                "addRecord b 9\n" +
                "addRecord c 10\n" +
                "printAll\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                addRecordMessage("s", "5") +
                addRecordMessage("b", "9") +
                addRecordMessage("c", "10") +
                "s 5, b 9, c 10\n\n", result);
    }

    @Test
    void clearWrongReplyAndNo() {
        setCommands("clear\n" +
                "????\n" +
                "exit\n" +
                "no\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                "Are you sure?? That will delete everything!\nPlease write yes or no.\n" +
                "Please write yes if you want to clear database and no otherwise.\n" +
                "Please write yes if you want to clear database and no otherwise.\n" +
                "Ok! Nothing was deleted!\n\n", result);
    }

    @Test
    void clearYesClearsDataBase() {
        setCommands("addRecord s 6\n" +
                "addRecord l 9\n" +
                "clear\n" +
                "yes\n" +
                "printAll\n" +
                "exit\n");

        PhonebookDataBase.main(argc);
        String result = getResult();
        assertEquals(intro +
                addRecordMessage("s", "6") +
                addRecordMessage("l", "9") +
                "Are you sure?? That will delete everything!\nPlease write yes or no.\n" +
                "Ok! Everything was deleted. It's all gone.\n\n" +
                "No records in database.\n\n", result);
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
        return "Ok! Record " + name + " " + phone + " has been added.\n\n";
    }
}