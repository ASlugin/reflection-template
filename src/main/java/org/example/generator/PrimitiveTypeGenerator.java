package org.example.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PrimitiveTypeGenerator {

    private static final Map<Class<?>, Class<?>> WRAPPER_TYPE_MAP;

    static {
        WRAPPER_TYPE_MAP = new HashMap<>(16);
        WRAPPER_TYPE_MAP.put(Integer.class, int.class);
        WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
        WRAPPER_TYPE_MAP.put(Character.class, char.class);
        WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
        WRAPPER_TYPE_MAP.put(Double.class, double.class);
        WRAPPER_TYPE_MAP.put(Float.class, float.class);
        WRAPPER_TYPE_MAP.put(Long.class, long.class);
        WRAPPER_TYPE_MAP.put(Short.class, short.class);
        WRAPPER_TYPE_MAP.put(Void.class, void.class);
    }

    public static boolean isPrimitiveType(Class<?> clazz) {
        return clazz.isPrimitive() || WRAPPER_TYPE_MAP.containsKey(clazz);
    }

    public static Object generateValueOfPrimitiveType(Class<?> clazz) {
        if (!isPrimitiveType(clazz)) {
            throw new UnsupportedOperationException(clazz.getName() + " is not a primitive type");
        }

        Class<?> primitiveType = clazz.isPrimitive() ? clazz : WRAPPER_TYPE_MAP.get(clazz);
        if (primitiveType == int.class)
            return new Random().nextInt();

        if (primitiveType == byte.class)
            return (byte) new Random().nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1);

        if (primitiveType == char.class)
            return (char) new Random().nextInt(Character.MIN_VALUE, Character.MAX_VALUE + 1);

        if (primitiveType == boolean.class)
            return new Random().nextBoolean();

        if (primitiveType == double.class)
            return new Random().nextDouble();

        if (primitiveType == float.class)
            return new Random().nextFloat();

        if (primitiveType == long.class)
            return new Random().nextLong();

        if (primitiveType == short.class)
            return (short) new Random().nextInt(Short.MIN_VALUE, Short.MAX_VALUE + 1);

        throw new UnsupportedOperationException("Unsupported primitive type: " + primitiveType.getTypeName());
    }
}
