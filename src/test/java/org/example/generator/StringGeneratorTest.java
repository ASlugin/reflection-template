package org.example.generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringGeneratorTest {

    @Test
    void generatesStringsWithinConfiguredBounds() {
        for (int i = 0; i < 50; i++) {
            assertValidString(StringGenerator.generateString());
        }
    }

    private static void assertValidString(String value) {
        assertNotNull(value);
        assertTrue(value.length() >= StringGenerator.MIN_LENGTH
                        && value.length() <= StringGenerator.MAX_LENGTH,
                "Generated string length should be within configured bounds");

        for (char character : value.toCharArray()) {
            assertTrue(character >= StringGenerator.MIN_CHARACTER
                            && character <= StringGenerator.MAX_CHARACTER,
                    "Generated characters should be within configured bounds");
        }
    }
}
