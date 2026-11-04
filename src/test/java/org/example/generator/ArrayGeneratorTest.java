package org.example.generator;

import org.example.classes.StateEnum;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrayGeneratorTest {

    private final Generator generator = new Generator();

    @Test
    void generatesPrimitiveArray() throws Exception {
        Object array = ArrayGenerator.generateArray(int[].class, generator);

        assertArrayTypeAndLength(array, int.class);
        for (int i = 0; i < Array.getLength(array); i++) {
            assertType(Integer.class,  Array.get(array, i));
        }
    }

    @Test
    void generatesStringArray() throws Exception {
        Object array = ArrayGenerator.generateArray(String[].class, generator);

        assertArrayTypeAndLength(array, String.class);
        for (int i = 0; i < Array.getLength(array); i++) {
            assertType(String.class,  Array.get(array, i));
        }
    }

    @Test
    void generatesEnumArray() throws Exception {
        Object array = ArrayGenerator.generateArray(StateEnum[].class, generator);

        assertArrayTypeAndLength(array, StateEnum.class);
        for (int i = 0; i < Array.getLength(array); i++) {
            assertTrue(Arrays.asList(StateEnum.values()).contains(Array.get(array, i)));
        }
    }

    @Test
    void rejectsNonArrayType() {
        assertThrows(IllegalArgumentException.class,
                () -> ArrayGenerator.generateArray(String.class, generator));
    }

    private static void assertArrayTypeAndLength(Object array, Class<?> componentType) {
        assertNotNull(array);
        assertTrue(array.getClass().isArray());
        assertEquals(componentType, array.getClass().getComponentType());

        int length = Array.getLength(array);
        assertTrue(length >= ArrayGenerator.MIN_LENGTH && length <= ArrayGenerator.MAX_LENGTH);
    }

    private static void assertType(Class<?> expectedValueType, Object value) {
        assertNotNull(value);
        assertEquals(expectedValueType, value.getClass());
    }
}
