package org.example.generator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;

public class ClassGenerator {

    private static final int MAX_OBJECT_DEPTH = 4;

    private ClassGenerator() {}

    public static Object generateClass(Class<?> clazz, Generator valueGenerator) {
        return generateClass(clazz, valueGenerator, 0);
    }

    public static Object generateClass(Class<?> clazz, Generator valueGenerator, int depth) {
        if (depth >= MAX_OBJECT_DEPTH) {
            return null;
        }

        if (Modifier.isInterface(clazz.getModifiers()) || Modifier.isAbstract(clazz.getModifiers())) {
            throw new UnsupportedOperationException();
        }

        Constructor<?>[] constructors = clazz.getConstructors();
        Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));

        Throwable lastFailure = null;
        for (Constructor<?> constructor : constructors) {
            try {
                Object[] arguments = generateConstructorArguments(constructor, valueGenerator, depth + 1);
                return constructor.newInstance(arguments);
            } catch (ReflectiveOperationException | RuntimeException exception) {
                lastFailure = exception;
            }
        }

        throw new IllegalArgumentException("No usable constructor for " + clazz.getName(), lastFailure);
    }

    private static Object[] generateConstructorArguments(
            Constructor<?> constructor,
            Generator valueGenerator,
            int depth
    ) throws ReflectiveOperationException {
        Type[] parameterTypes = constructor.getGenericParameterTypes();
        Object[] arguments = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            arguments[i] = valueGenerator.generateValueOfType(parameterTypes[i], depth);
        }
        return arguments;
    }
}
