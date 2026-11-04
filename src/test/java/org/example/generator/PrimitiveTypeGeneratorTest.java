package org.example.generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrimitiveTypeGeneratorTest {

    private static final Class<?>[][] PRIMITIVE_AND_WRAPPER_TYPES = {
            {int.class, Integer.class},
            {byte.class, Byte.class},
            {char.class, Character.class},
            {boolean.class, Boolean.class},
            {double.class, Double.class},
            {float.class, Float.class},
            {long.class, Long.class},
            {short.class, Short.class}
    };

    @Test
    void recognizesPrimitiveTypes() {
        for (Class<?>[] pair : PRIMITIVE_AND_WRAPPER_TYPES) {
            Class<?> primitiveType = pair[0];
            assertTrue(PrimitiveTypeGenerator.isPrimitiveType(primitiveType),
                    primitiveType.getTypeName() + " should be recognized as primitive");
        }

        assertTrue(PrimitiveTypeGenerator.isPrimitiveType(void.class));
    }

    @Test
    void recognizesWrapperTypes() {
        for (Class<?>[] pair : PRIMITIVE_AND_WRAPPER_TYPES) {
            Class<?> wrapperType = pair[1];
            assertTrue(PrimitiveTypeGenerator.isPrimitiveType(wrapperType),
                    wrapperType.getTypeName() + " should be recognized as a wrapper");
        }

        assertTrue(PrimitiveTypeGenerator.isPrimitiveType(Void.class));
    }

    @Test
    void doesNotRecognizeNonPrimitiveTypes() {
        Class<?>[] nonPrimitiveTypes = {String.class, Object.class};

        for (Class<?> type : nonPrimitiveTypes) {
            assertFalse(PrimitiveTypeGenerator.isPrimitiveType(type),
                    type.getTypeName() + " should not be recognized as primitive");
        }
    }

    @Test
    void generatesValuesOfExpectedTypesForPrimitiveClasses() {
        for (Class<?>[] pair : PRIMITIVE_AND_WRAPPER_TYPES) {
            assertGeneratedValue(pair[0], pair[1]);
        }
    }

    @Test
    void generatesValuesOfExpectedTypesForWrapperClasses() {
        for (Class<?>[] pair : PRIMITIVE_AND_WRAPPER_TYPES) {
            assertGeneratedValue(pair[1], pair[1]);
        }
    }

    @Test
    void rejectsNonPrimitiveTypeWhenGeneratingValue() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PrimitiveTypeGenerator.generateValueOfPrimitiveType(String.class)
        );

        assertTrue(exception.getMessage().contains(String.class.getName()));
    }

    @Test
    void doesNotGenerateVoidValues() {
        Class<?>[] voidTypes = {void.class, Void.class};

        for (Class<?> type : voidTypes) {
            assertThrows(IllegalArgumentException.class,
                    () -> PrimitiveTypeGenerator.generateValueOfPrimitiveType(type),
                    type.getTypeName() + " cannot have a generated value");
        }
    }

    private static void assertGeneratedValue(Class<?> requestedType, Class<?> expectedValueType) {
        Object value = PrimitiveTypeGenerator.generateValueOfPrimitiveType(requestedType);

        assertNotNull(value);
        assertEquals(expectedValueType, value.getClass());
    }
}
