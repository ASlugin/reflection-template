package org.example.generator;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorTest {

    private static final Generator generator = new Generator();

    @Test
    void generateValueOfType_PrimitiveAndWrapperTypes() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[][] primitiveAndWrapperClasses = {
                {int.class, Integer.class},
                {byte.class, Byte.class},
                {char.class, Character.class},
                {boolean.class, Boolean.class},
                {double.class, Double.class},
                {float.class, Float.class},
                {long.class, Long.class},
                {short.class, Short.class}
        };

        for (Class<?>[] typePair : primitiveAndWrapperClasses) {
            Class<?> primitiveType  = typePair[0];
            Class<?> wrapperType = typePair[1];

            Object result = generator.generateValueOfType(primitiveType);
            assertType(wrapperType, result);

            result = generator.generateValueOfType(wrapperType);
            assertType(wrapperType, result);
        }
    }

    @Test
    void generateValueOfType_String() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object result = generator.generateValueOfType(String.class);
        assertType(String.class, result);

        String generatedString = (String) result;
        assertTrue(generatedString.length() >= StringGenerator.MIN_LENGTH
                        && generatedString.length() <= StringGenerator.MAX_LENGTH);

        for (char character : generatedString.toCharArray()) {
            assertTrue(character >= StringGenerator.MIN_CHARACTER
                    && character <= StringGenerator.MAX_CHARACTER);
        }
    }

    private static void assertType(Class<?> expectedValueType, Object value) {
        assertNotNull(value);
        assertEquals(expectedValueType, value.getClass());
    }
}
