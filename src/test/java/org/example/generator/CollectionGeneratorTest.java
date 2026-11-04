package org.example.generator;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionGeneratorTest {

    private final Generator generator = new Generator();

    @Test
    void createsArrayListAndFillsItWithBytes() throws Exception {
        Object result = CollectionGenerator.generateCollection(ArrayList.class, generator);

        assertInstanceOf(ArrayList.class, result);
        assertByteCollection((Collection<?>) result);
    }

    @Test
    void usesGeneratedArgumentsForCollectionConstructor() throws Exception {
        IntegerConstructorCollection result = (IntegerConstructorCollection)
                CollectionGenerator.generateCollection(IntegerConstructorCollection.class, generator);

        assertNotNull(result.constructorArgument);
        assertByteCollection(result);
    }

    @Test
    void rejectsNonCollectionClass() {
        assertThrows(IllegalArgumentException.class,
                () -> CollectionGenerator.generateCollection(String.class, generator));
    }

    private static void assertByteCollection(Collection<?> collection) {
        assertFalse(collection.isEmpty());
        assertTrue(collection.size() >= CollectionGenerator.MIN_LENGTH
                        && collection.size() <= CollectionGenerator.MAX_LENGTH);

        for (Object value : collection) {
            assertNotNull(value);
            assertEquals(Byte.class, value.getClass());
        }
    }

    public static class IntegerConstructorCollection extends ArrayList<Byte> {
        private final Integer constructorArgument;

        public IntegerConstructorCollection(Integer constructorArgument) {
            this.constructorArgument = constructorArgument;
        }
    }
}
