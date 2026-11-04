package org.example.generator;

import org.example.classes.Rectangle;
import org.example.classes.StateEnum;
import org.example.classes.Cart;
import org.example.classes.Product;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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

    @Test
    void generateValueOfType_Map() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object result = generator.generateValueOfType(Map.class);

        assertNotNull(result);
        assertTrue(result instanceof Map<?, ?>);

        Map<?, ?> generatedMap = (Map<?, ?>) result;
        assertFalse(generatedMap.isEmpty());
        for (Map.Entry<?, ?> entry : generatedMap.entrySet()) {
            assertEquals(Byte.class, entry.getKey().getClass());
            assertEquals(Byte.class, entry.getValue().getClass());
        }
    }

    @Test
    void generateValueOfType_Class() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object result = generator.generateValueOfType(Rectangle.class);

        Rectangle rectangle = assertInstanceOf(Rectangle.class, result);

        Field lengthField = getDeclaredField(Rectangle.class, "length");
        Field widthField = getDeclaredField(Rectangle.class, "width");
        double length = assertGeneratedDoubleField(lengthField, rectangle);
        double width = assertGeneratedDoubleField(widthField, rectangle);

        assertEquals(length * width, rectangle.getArea(), 0.0);
        assertEquals(2 * (length + width), rectangle.getPerimeter(), 0.0);
    }

    @Test
    void generateValueOfType_Cart() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Cart cart = assertInstanceOf(Cart.class, generator.generateValueOfType(Cart.class));

        assertFalse(cart.getItems().isEmpty());
        assertTrue(cart.getItems().size() >= CollectionGenerator.MIN_LENGTH
                && cart.getItems().size() <= CollectionGenerator.MAX_LENGTH);

        for (Product product : cart.getItems()) {
            assertNotNull(product.getName());
            assertTrue(product.getName().length() >= StringGenerator.MIN_LENGTH
                    && product.getName().length() <= StringGenerator.MAX_LENGTH);
            for (char character : product.getName().toCharArray()) {
                assertTrue(character >= StringGenerator.MIN_CHARACTER
                        && character <= StringGenerator.MAX_CHARACTER);
            }
            assertTrue(product.getPrice() >= 0.0 && product.getPrice() < 1.0);
        }
    }

    private static Field getDeclaredField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException exception) {
            throw new AssertionError("Expected field " + name + " on " + clazz.getName(), exception);
        }
    }

    private static double assertGeneratedDoubleField(Field field, Object instance) throws IllegalAccessException {
        assertEquals(double.class, field.getType());
        field.setAccessible(true);

        Object value = field.get(instance);
        assertInstanceOf(Double.class, value);
        double generatedValue = (Double) value;
        assertTrue(generatedValue >= 0.0 && generatedValue < 1.0);
        return generatedValue;
    }

    private static void assertType(Class<?> expectedValueType, Object value) {
        assertNotNull(value);
        assertEquals(expectedValueType, value.getClass());
    }
}
