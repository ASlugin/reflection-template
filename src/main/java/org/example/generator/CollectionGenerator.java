package org.example.generator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

public class CollectionGenerator {

    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = 5;

    private static final Class<?> DEFAULT_ELEMENT_TYPE = Byte.class;

    private static final Class<?>[] candidateTypesForInterfaces = {
            LinkedHashSet.class,
            ArrayList.class,
            ArrayDeque.class,
            PriorityQueue.class
    };

    private CollectionGenerator() {}

    public static Object generateCollection(Class<?> clazz, Generator valueGenerator) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return generateCollection(clazz, valueGenerator, DEFAULT_ELEMENT_TYPE, 0);
    }

    public static Object generateCollection(Class<?> clazz, Generator valueGenerator, Type elementType)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return generateCollection(clazz, valueGenerator, elementType, 0);
    }

    static Object generateCollection(Class<?> clazz, Generator valueGenerator, Type elementType, int depth)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!Collection.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(clazz.getName() + " is not a Collection");
        }

        Class<?> concreteType = resolveConcreteType(clazz);
        Collection<Object> collection = instantiateCollection(concreteType, valueGenerator);
        Type resolvedElementType = elementType == null ? DEFAULT_ELEMENT_TYPE : elementType;

        int size = (Integer) PrimitiveTypeGenerator.generateValueOfPrimitiveType(int.class, MIN_LENGTH, MAX_LENGTH);
        for (int i = 0; i < size; i++) {
            collection.add(valueGenerator.generateValueOfType(resolvedElementType, depth));
        }
        return collection;

    }

    private static Class<?> resolveConcreteType(Class<?> clazz) {
        if (!Modifier.isInterface(clazz.getModifiers()) && !Modifier.isAbstract(clazz.getModifiers())) {
            return clazz;
        }

        for (Class<?> candidateType : candidateTypesForInterfaces) {
            if (clazz.isAssignableFrom(candidateType)) {
                return candidateType;
            }
        }
        throw new IllegalArgumentException("No supported concrete implementation for " + clazz.getName());
    }

    private static Collection<Object> instantiateCollection(Class<?> clazz, Generator valueGenerator) {
        Constructor<?>[] constructors = clazz.getConstructors();
        Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));

        Throwable lastFailure = null;
        for (Constructor<?> constructor : constructors) {
            try {
                Object[] arguments = generateConstructorArguments(constructor, valueGenerator);
                return (Collection<Object>) constructor.newInstance(arguments);
            } catch (ReflectiveOperationException | RuntimeException exception) {
                lastFailure = exception;
            }
        }

        throw new IllegalArgumentException("No usable constructor for " + clazz.getName(), lastFailure);
    }

    private static Object[] generateConstructorArguments(Constructor<?> constructor, Generator valueGenerator) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            arguments[i] = valueGenerator.generateValueOfType(parameterType);
        }
        return arguments;
    }
}
