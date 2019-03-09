package ru.hse.reflector;

import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReflectorTest {
    /**
     * Empty classes
     */
    private static class EmptyClassA {
    }
    private static class EmptyClassB {
    }

    /**
     * Two classes with equal fields.
     */
    private static class SameFieldsA {
        private static int a;
        public static transient volatile Integer b;
        final List<?> c = null;
        List<List<?>> d;
    }
    private static class SameFieldsB {
        List<List<?>> d;
        public static transient volatile Integer b;
        private static int a;
        final List<? extends Object> c = null;
    }

    /**
     * Two classes with different fields.
     */
    private static class DifferentFieldsA {
        public int a;
        protected transient Integer b;
        final List<? extends Integer> c = null;
        int d0;
        DifferentFieldsA e;
    }
    private static class DifferentFieldsB {
        private int a;
        protected static transient Integer b;
        final List<?> c = null;
        int d1;
        DifferentFieldsB e;
    }

    /**
     * Two classes with equal methods.
     */
    private abstract static class SameMethodsA {
        private static <E extends Object, R>  void a(List<? extends Object> list) {
        }

        protected <A, B> int b(List<? extends A> list, Integer integer, int q) {
            return 0;
        }

        abstract int c();
    }
    private abstract static class SameMethodsB {
        private static <E, P extends Object> void a(List<?> list2) {
        }

        protected <A, B> int b(List<? extends A> list2, Integer integer2, int q2) {
            return 1;
        }

        abstract int c();
    }

    /**
     * Two classes with different methods.
     */
    private static class DifferentMethodsA {
        private void a() {}

        private <E> void b(E a) {}

        private <E> void c(Integer integer) {}

        private void d() {}

        public void e(Integer a, int b) {}
    }
    private static class DifferentMethodsB {
        void a() {}

        private void b(Integer a) {}

        private <E> void c(E e) {}

        private static void d() {}

        public void e(int a, Integer b) {}
    }

    /**
     * Two classes with either equal inner classes (contains either equal and non-equal fields and methods) and non-equal
     * inner classes.
     */
    private static class InnerClassesA {
        class InnerClass {
            public int a;
            private int b;
            public void c() {}
            private void d(Integer a) {}
        }
    }
    private static class InnerClassesB {
        class InnerClass {
            public int a;
            private volatile int b;
            public void c() {}
            private int d(Integer a) {return 0;}
        }

        public abstract class OtherClass {
            int something;
        }
    }


    @Test
    void diffEmptyClassesIsEmpty() {
        assertEquals("", Reflector.diffClasses(EmptyClassA.class, EmptyClassB.class));
    }

    @Test
    void diffSameFieldsInClassesReturnsEmptyString() {
        assertEquals("", Reflector.diffClasses(SameFieldsA.class, SameFieldsB.class));
    }

    @Test
    void diffDifferentFieldsReturnThem() {
        assertEquals("public int ru.hse.reflector.ReflectorTest$DifferentFieldsA.a\n"
                        + "protected transient java.lang.Integer ru.hse.reflector.ReflectorTest$DifferentFieldsA.b\n"
                        + "final java.util.List<? extends java.lang.Integer> ru.hse.reflector.ReflectorTest$DifferentFieldsA.c\n"
                        + "int ru.hse.reflector.ReflectorTest$DifferentFieldsA.d0\n"
                        + "ru.hse.reflector.ReflectorTest$DifferentFieldsA ru.hse.reflector.ReflectorTest$DifferentFieldsA.e\n"
                        + "private int ru.hse.reflector.ReflectorTest$DifferentFieldsB.a\n"
                        + "protected static transient java.lang.Integer ru.hse.reflector.ReflectorTest$DifferentFieldsB.b\n"
                        + "final java.util.List<?> ru.hse.reflector.ReflectorTest$DifferentFieldsB.c\n"
                        + "int ru.hse.reflector.ReflectorTest$DifferentFieldsB.d1\n"
                        + "ru.hse.reflector.ReflectorTest$DifferentFieldsB ru.hse.reflector.ReflectorTest$DifferentFieldsB.e\n"
                , Reflector.diffClasses(DifferentFieldsA.class, DifferentFieldsB.class));
    }

    @Test
    void diffSameMethodsReturnsEmptyString() {
        assertEquals("", Reflector.diffClasses(SameMethodsA.class, SameMethodsB.class));
    }

    @Test
    void diffDifferentMethodsReturnsThem() {
        List<String> result = Arrays.asList(Reflector.diffClasses(DifferentMethodsA.class, DifferentMethodsB.class).split("\n"));
        assertEquals(10, result.size());
    }

    @Test
    void diffInInnerClasses() {
        List<String> result = Arrays.asList(Reflector.diffClasses(InnerClassesA.class, InnerClassesB.class).split("\n"));
        assertEquals(5, result.size());
    }

    private void checkPrintedClass(Class<?> testClass) throws IOException, ClassNotFoundException {
        Reflector.printStructure(testClass);

        var file = new File("./someClass.java");
        var classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
        var resultClass = classLoader.loadClass(testClass.getName());
        assertEquals("", Reflector.diffClasses(testClass, resultClass));
    }

    @Test
    void printEmptyClass() throws IOException, ClassNotFoundException {
        checkPrintedClass(EmptyClassA.class);
        checkPrintedClass(EmptyClassB.class);
    }

    @Test
    void printFields() throws IOException, ClassNotFoundException {
        checkPrintedClass(SameFieldsA.class);
        checkPrintedClass(SameFieldsB.class);
        checkPrintedClass(DifferentFieldsA.class);
        checkPrintedClass(DifferentFieldsB.class);
    }

    @Test
    void printMethods() throws IOException, ClassNotFoundException {
        checkPrintedClass(SameMethodsA.class);
        checkPrintedClass(SameMethodsB.class);
        checkPrintedClass(DifferentMethodsA.class);
        checkPrintedClass(DifferentMethodsB.class);
    }

    @Test
    void printInnerClasses() throws IOException, ClassNotFoundException {
        checkPrintedClass(InnerClassesA.class);
        checkPrintedClass(InnerClassesB.class);
    }
}