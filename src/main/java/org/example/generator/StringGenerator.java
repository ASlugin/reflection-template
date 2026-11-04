package org.example.generator;

public class StringGenerator {

    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = 20;
    public static final char MIN_CHARACTER = 'a';
    public static final char MAX_CHARACTER = 'z';

    private StringGenerator() {}

    public static String generateString() {
        int length = (Integer) PrimitiveTypeGenerator.generateValueOfPrimitiveType(int.class, MIN_LENGTH, MAX_LENGTH);

        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char character = (Character) PrimitiveTypeGenerator
                    .generateValueOfPrimitiveType(char.class, MIN_CHARACTER, MAX_CHARACTER);
            result.append(character);
        }
        return result.toString();
    }
}
