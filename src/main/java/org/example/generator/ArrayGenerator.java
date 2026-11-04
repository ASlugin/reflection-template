package org.example.generator;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

public class ArrayGenerator {

    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = 20;

    private ArrayGenerator() {}

    public static Object generateArray(Class<?> clazz, Generator valueGenerator) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!clazz.isArray()) {
            throw new IllegalArgumentException(clazz.getName() + " is not an array");
        }

        int length = (int) PrimitiveTypeGenerator.generateValueOfPrimitiveType(int.class, MIN_LENGTH, MAX_LENGTH);
        Class<?> componentType = clazz.getComponentType();
        Object array = Array.newInstance(componentType, length);

        for (int i = 0; i < length; i++) {
            Object value = valueGenerator.generateValueOfType(componentType);
            Array.set(array, i, value);
        }
        return array;
    }

}
