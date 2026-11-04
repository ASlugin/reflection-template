package org.example.generator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class MapGenerator {

    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 5;

    private static final Class<?> DEFAULT_KEY_TYPE = Byte.class;
    private static final Class<?> DEFAULT_VALUE_TYPE = Byte.class;

    private static final Class<?>[] IMPLEMENTATIONS = {
            TreeMap.class,
            LinkedHashMap.class,
            ConcurrentHashMap.class,
            ConcurrentSkipListMap.class
    };

    private MapGenerator() {}

    public static Object generateMap(Class<?> clazz, Generator valueGenerator)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!Map.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(clazz.getName() + " is not a Map");
        }

        Class<?> concreteType = resolveConcreteType(clazz);
        Map<Object, Object> map = instantiateMap(concreteType, valueGenerator);

        int size = (Integer) PrimitiveTypeGenerator.generateValueOfPrimitiveType(int.class, MIN_SIZE, MAX_SIZE);
        for (int i = 0; i < size; i++) {
            Object key = valueGenerator.generateValueOfType(DEFAULT_KEY_TYPE);
            Object value = valueGenerator.generateValueOfType(DEFAULT_VALUE_TYPE);
            map.put(key, value);
        }
        return map;
    }

    private static Class<?> resolveConcreteType(Class<?> clazz) {
        if (!Modifier.isInterface(clazz.getModifiers()) && !Modifier.isAbstract(clazz.getModifiers())) {
            return clazz;
        }
        for (Class<?> implementation : IMPLEMENTATIONS) {
            if (clazz.isAssignableFrom(implementation)) {
                return implementation;
            }
        }
        throw new IllegalArgumentException("No supported concrete implementation for " + clazz.getName());
    }

    private static Map<Object, Object> instantiateMap(Class<?> clazz, Generator valueGenerator) {
        Constructor<?>[] constructors = clazz.getConstructors();
        Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));

        Throwable lastFailure = null;
        for (Constructor<?> constructor : constructors) {
            try {
                Object[] arguments = generateConstructorArguments(constructor, valueGenerator);
                return (Map<Object, Object>) constructor.newInstance(arguments);
            } catch (ReflectiveOperationException | RuntimeException exception) {
                lastFailure = exception;
            }
        }

        throw new IllegalArgumentException("No usable constructor for " + clazz.getName(), lastFailure);
    }

    private static Object[] generateConstructorArguments(Constructor<?> constructor, Generator valueGenerator)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] arguments = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            arguments[i] = valueGenerator.generateValueOfType(parameterTypes[i]);
        }
        return arguments;
    }
}
