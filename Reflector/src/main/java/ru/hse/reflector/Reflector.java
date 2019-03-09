package ru.hse.reflector;

import java.io.*;
import java.lang.reflect.*;
import java.sql.Array;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Reflector {
    private static String defaultIndent = "    ";

    public static void printStructure(Class<?> someClass) throws IOException {
        var file = new File("someClass.java");
        var wasCreated = file.createNewFile();
        if (!wasCreated && !file.canWrite()) {
            throw new RuntimeException("Can't create or write to file someClass.java");
        }

        try (var writer = new FileWriter(file)) {
            printClass(someClass, writer, "");
        }
    }

    public static String diffClasses(Class<?> a, Class<?> b) {
        return diffBaseClass(a, b) + diffBaseClass(b, a);
    }

    private static String diffBaseClass(Class<?> base, Class<?> target) {
        var result = new StringBuilder();

        for (var baseField : base.getDeclaredFields()) {
            if (baseField.getName().matches("this[$][0-9]+")) {
                continue;
            }

            boolean matches = false;
            for (var targetField : target.getDeclaredFields()) {
                if (equalFields(baseField, targetField)) {
                    matches = true;
                    break;
                }
            }

            if (!matches) {
                result.append(baseField.toGenericString()).append("\n");
            }
        }

        for (var baseMethod : base.getDeclaredMethods()) {
            boolean matches = false;
            for (var targetMethod : target.getDeclaredMethods()) {
                if (equalMethods(baseMethod, targetMethod)) {
                    matches = true;
                    break;
                }
            }

            if (!matches) {
                result.append(baseMethod.toGenericString()).append("\n");
            }
        }

        for (var baseClass : base.getDeclaredClasses()) {
            boolean matches = false;
            for (var targetClass : target.getDeclaredClasses()) {
                if (equalClasses(baseClass, targetClass)) {
                    matches = true;
                    result.append(diffBaseClass(baseClass, targetClass));
                    break;
                }
            }

            if (!matches) {
                result.append(baseClass.toGenericString()).append("\n");
            }
        }

        return result.toString();
    }

    private static boolean equalFields(Field baseField, Field targetField) {
        return baseField.getName().equals(targetField.getName())
               && baseField.getModifiers() == targetField.getModifiers()
               && baseField.getGenericType().equals(targetField.getGenericType());
    }

    private static boolean equalMethods(Method baseMethod, Method targetMethod) {
        return baseMethod.getModifiers() == targetMethod.getModifiers()
                && baseMethod.getName().equals(targetMethod.getName())
                && Arrays.equals(baseMethod.getParameterTypes(), targetMethod.getParameterTypes())
                && baseMethod.getGenericReturnType().equals(targetMethod.getGenericReturnType());
    }

    private static boolean equalClasses(Class<?> baseClass, Class<?> targetClass) {
        return baseClass.getModifiers() == targetClass.getModifiers()
                && baseClass.getSimpleName().equals(targetClass.getSimpleName())
                && baseClass.getGenericSuperclass().equals(targetClass.getGenericSuperclass())
                && Arrays.equals(baseClass.getGenericInterfaces(), targetClass.getGenericInterfaces());
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
}
