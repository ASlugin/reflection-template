package org.example.generator;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapGeneratorTest {

    private final Generator generator = new Generator();

    @Test
    void createsConcreteMapAndFillsItWithBytePairs() throws Exception {
        Object result = MapGenerator.generateMap(HashMap.class, generator);

        assertInstanceOf(HashMap.class, result);
        assertByteMap((Map<?, ?>) result);
    }

    @Test
    void choosesConcreteImplementationForMapInterface() throws Exception {
        Object result = MapGenerator.generateMap(Map.class, generator);

        assertInstanceOf(Map.class, result);
        assertByteMap((Map<?, ?>) result);
    }

    @Test
    void usesGeneratedArgumentsForMapConstructor() throws Exception {
        IntegerConstructorMap result = (IntegerConstructorMap)
                MapGenerator.generateMap(IntegerConstructorMap.class, generator);

        assertNotNull(result.constructorArgument);
        assertByteMap(result);
    }

    @Test
    void rejectsNonMapClass() {
        assertThrows(IllegalArgumentException.class,
                () -> MapGenerator.generateMap(String.class, generator));
    }

    private static void assertByteMap(Map<?, ?> map) {
        assertFalse(map.isEmpty());
        assertTrue(map.size() <= MapGenerator.MAX_SIZE);

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            assertEquals(Byte.class, entry.getKey().getClass());
            assertEquals(Byte.class, entry.getValue().getClass());
        }
    }

    public static class IntegerConstructorMap extends HashMap<Byte, Byte> {
        private final Integer constructorArgument;

        public IntegerConstructorMap(Integer constructorArgument) {
            this.constructorArgument = constructorArgument;
        }
    }
}
