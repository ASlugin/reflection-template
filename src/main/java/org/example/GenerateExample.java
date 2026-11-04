package org.example;

import org.example.classes.Example;
import org.example.classes.StateEnum;
import org.example.generator.Generator;

public class GenerateExample {
    public static void main(String[] args) {
        var gen = new Generator();
        try {
            System.out.printf("Byte: %s\n", gen.generateValueOfType(Byte.class));
            System.out.printf("Short: %s\n", gen.generateValueOfType(Short.class));
            System.out.printf("Integer: %s\n", gen.generateValueOfType(Integer.class));
            System.out.printf("Long: %s\n", gen.generateValueOfType(Long.class));
            System.out.printf("Double: %s\n", gen.generateValueOfType(Double.class));
            System.out.printf("Float: %s\n", gen.generateValueOfType(Float.class));
            System.out.printf("Boolean: %s\n", gen.generateValueOfType(Boolean.class));
            System.out.printf("Character: %s\n", gen.generateValueOfType(Character.class));
            System.out.printf("Enum: %s\n", gen.generateValueOfType(StateEnum.class));

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}