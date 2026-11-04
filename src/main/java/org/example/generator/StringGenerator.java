package org.example.generator;

public final class StringGenerator {

    static final int MIN_LENGTH = 1;
    static final int MAX_LENGTH = 20;
    static final char MIN_CHARACTER = 'a';
    static final char MAX_CHARACTER = 'z';

    private StringGenerator() {
    }

    public static String generateString() {
        int generatedLength = (Integer) PrimitiveTypeGenerator.generateValueOfPrimitiveType(int.class);
        int length = Math.floorMod(generatedLength, MAX_LENGTH - MIN_LENGTH + 1) + MIN_LENGTH;

        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char generatedCharacter = (Character) PrimitiveTypeGenerator.generateValueOfPrimitiveType(char.class);
            char character = (char) (Math.floorMod(generatedCharacter, MAX_CHARACTER - MIN_CHARACTER + 1) + MIN_CHARACTER);
            result.append(character);
        }
        return result.toString();
    }
}
