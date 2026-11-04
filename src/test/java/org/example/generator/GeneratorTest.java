package org.example.generator;

import org.example.classes.StateEnum;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Test
    void generateValueOfType_Enum() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object result = generator.generateValueOfType(StateEnum.class);

        assertType(StateEnum.class, result);
        assertTrue(Arrays.asList(StateEnum.values()).contains(result));
    }

    @Test
    void generateValueOfType_Array() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object result = generator.generateValueOfType(int[].class);

        assertType(int[].class, result);
        int length = Array.getLength(result);
        assertTrue(length >= ArrayGenerator.MIN_LENGTH && length <= ArrayGenerator.MAX_LENGTH);

        for (int i = 0; i < length; i++) {
            assertType(Integer.class, Array.get(result, i));
        }
    }

    @Test
    void generateValueOfType_Collection() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object result = generator.generateValueOfType(List.class);

        assertType(ArrayList.class, result);
        List<?> generatedList = (List<?>) result;
        assertFalse(generatedList.isEmpty());

        for (Object value : generatedList) {
            assertEquals(Byte.class, value.getClass());
        }
    }

    private static void assertType(Class<?> expectedValueType, Object value) {
        assertNotNull(value);
        assertEquals(expectedValueType, value.getClass());
    }
}
