package ru.hse.hw4.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Scanner;

/**
 *
 */
public class PhonebookDataBase {

    /**
     *
     */
    public static void main(String[] argc) {
        runInteraction(System.in, System.out);
    }

    /**
     *
     */
    private static void runInteraction(@NotNull InputStream inputStream, @NotNull OutputStream outputStream) {
        var inputScanner = new Scanner(inputStream);
        var printWriter = new PrintWriter(outputStream);
        printWriter.println("Write help to get help");
        boolean stopInteraction = false; //
        while (!stopInteraction) {
            if (inputScanner.hasNextLine()) {
                var commandScanner = new Scanner(inputScanner.nextLine());
                switch (commandScanner.next()) {
                    case ("help"): {
                        printWriter.println("Please use one of the following commands:\n" +
                                "exit                                          exit the program\n" +
                                "addRecord name phoneNumber                    adds new record with given name and phone number\n" +
                                "findPhones {-byName name}                     finds all phones by given parameter\n" +
                                "findNames  {-byPhone phoneNumber}             finds all names by given parameter\n" +
                                "deleteRecord name phoneNumber                 deletes given record from the base\n" +
                                "changeName name phoneNumber newName           changes name to newName for given record\n" +
                                "changePhone name phoneNumber newPhoneNumber   changes phoneNumber to newPhoneNumber for given record\n" +
                                "printAll                                      prints all record in the base");
                        break;
                    }

                    case ("exit"): {
                        stopInteraction = true;
                        break;
                    }

                    case ("addRecord"): {
                        String[] parameters = getParameters(commandScanner, 2);
                        if (parameters == null) {
                            printWriter.println("name or phone number is missing. No record has been added.");
                            break;
                        }

                        String name = parameters[0], phone = parameters[1];

                        //TODO

                        break;
                    }

                    case ("findPhones"): {
                        String key = getParameter(commandScanner);
                        if (key == null) {
                            printWriter.println("No key specified for search");
                            break;
                        }

                        switch (key) {
                            case ("-byName"):
                                String name = getParameter(commandScanner);
                                if (name == null) {
                                    printWriter.println("no name specified for -byName search");
                                    break;
                                }

                                //TODO
                                break;
                            default:
                                printWriter.println("unknown key");
                        }
                        break;
                    }

                    case ("findNames"): {
                        String findNamesKey = getParameter(commandScanner);
                        if (findNamesKey == null) {
                            printWriter.println("key is missing");
                            break;
                        }

                        switch (findNamesKey) {
                            case ("-byPhone"):
                                String phone = getParameter(commandScanner);
                                if (phone == null) {
                                    printWriter.println("no phone number specified for -byPhone search");
                                }

                                //TODO
                                break;
                            default:
                                printWriter.println("unknown key");
                        }
                        break;
                    }

                    case ("deleteRecord"): {
                        String[] parameters = getParameters(commandScanner, 3);
                        if (parameters == null) {
                            printWriter.println("name or phone not specified, no record has been deleted");
                            break;
                        }

                        String deleteName = parameters[0], deletePhone = parameters[1];

                        //TODO

                        break;
                    }

                    case ("changeName"): {
                        String[] parameters = getParameters(commandScanner, 3);
                        if (parameters == null) {
                            printWriter.println("name, phone or newName not specified, no record has been changed");
                            break;
                        }

                        String name = parameters[0], phone = parameters[1], newName = parameters[2];

                        //TODO

                        break;
                    }

                    case("changePhone"): {
                        String[] parameters = getParameters(commandScanner, 3);
                        if (parameters == null) {
                            printWriter.println("name, phone or newPhone was not specified, no record has been changed");
                            break;
                        }

                        String name = parameters[0], phone = parameters[1], newPhone = parameters[2];
                        //TODO
                        break;
                    }

                    case ("printAll"): {
                        //TODO
                    }
                }
            }
        }
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

