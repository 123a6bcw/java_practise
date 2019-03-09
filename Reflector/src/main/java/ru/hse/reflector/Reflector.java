package ru.hse.reflector;

import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Reflector {
    static String defaultIndent = "    ";

    public static void printStructure(Class<?> someClass) throws IOException {
        var file = new File("someClass.java");
        var wasCreated = file.createNewFile();
        if (!wasCreated && !file.canWrite()) {
            throw new RuntimeException("Can't create file someClass.java");
        }

        try (var writer = new FileWriter(file)) {
            printClass(someClass, writer, "");
        }
    }

    private static void printClass(Class<?> someClass, FileWriter writer, String indent) throws IOException {
        printHeading(someClass, writer, indent);
        writer.write(" {\n");

        printFields(someClass, writer, indent + defaultIndent);
        writer.write("\n");
        printMethods(someClass, writer, indent + defaultIndent);
        printClasses(someClass, writer, indent + defaultIndent);

        writer.write(indent + "}");
    }

    private static void printFields(Class<?> someClass, FileWriter writer, String indent) throws IOException {
        for (var field : someClass.getDeclaredFields()) {
            if (!field.getName().matches("this[$][0-9]+")) {
                writer.write(indent);
                writer.write(field.toGenericString());
                writer.write(";\n");
            }
        }
    }

    private static void printMethods(Class<?> someClass, FileWriter writer, String indent) throws IOException {
        for (var method : someClass.getDeclaredMethods()) {
            writer.write(indent);

            writer.write(method.toGenericString().
                    replaceFirst("[(].*[)]",
                            "("
                            + Arrays.stream(method.getParameters()).map(Parameter::toString).collect(Collectors.joining(", "))
                            + ")")
                    + " {\n");

            if (!method.getReturnType().toString().equals("void")) {
                writer.write(indent + defaultIndent);
                if (method.getReturnType().isPrimitive()) {
                    writer.write("return 0; \n");
                } else {
                    writer.write("return null;\n");
                }
            }

            writer.write(indent + "}\n\n");
        }
    }

    private static void printClasses(Class<?> someClass, FileWriter writer, String indent) throws IOException {
        for (var subClass : someClass.getDeclaredClasses()) {
            printClass(subClass, writer, indent);
            writer.write("\n");
        }
    }

    private static void printHeading(Class<?> someClass, FileWriter writer, String indent) throws IOException {
        writer.write(indent);
        writer.write(someClass.toGenericString());

        if (someClass.getSuperclass() != null) {
            writer.write(" extends " + someClass.getGenericSuperclass().getTypeName());
        }

        if (someClass.getInterfaces().length > 0) {
            writer.write(" implements ");
            writer.write(Arrays.stream(someClass.getGenericInterfaces()).map(Type::getTypeName).collect(Collectors.joining(", ")));
        }
    }

    public static class closs<E> extends pomidorka<E> implements Cloneable, Closeable, ME_TOO_XD<E> {
        static int test1;
        private int someInt;
        private closs<E> closa;
        protected String strong;

        public static <E> int doSomethingElse(Integer integer) {
            return 0;
        }

        public static <E> int doSomething(Class<? extends E> pokazuha, Class<E> pomoshnik) {
            return test1;
        }

        public void close() throws IOException {
        }

        public class iLikeEatMeat<R> {
            private int test2;

            private class ILikeAnimanls<O> implements Closeable {
                public void close() throws IOException {

                }
            }
        }
    }

    public static interface IAMINTERFACE {

    }

    public static interface ME_TOO_XD<E> extends IAMINTERFACE {

    }

    protected static class pomidorka<Pomidor> {

    }
}
