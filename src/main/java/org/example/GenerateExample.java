package org.example;

import org.example.classes.Cart;
import org.example.classes.Example;
import org.example.classes.StateEnum;
import org.example.generator.Generator;

import java.util.*;

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

            byte[] array = (byte[]) gen.generateValueOfType(byte[].class);
            System.out.printf("Array of bytes: %s\n", Arrays.toString(array));

            Collection<Object> list = (Collection<Object>) gen.generateValueOfType(Set.class);
            System.out.printf("List: type=%s %s\n", list.getClass().getTypeName(), list);

            Map<Object, Object> map = (Map<Object, Object>) gen.generateValueOfType(LinkedHashMap.class);
            System.out.printf("Map: type=%s %s\n", map.getClass().getTypeName(), map);

            Example example = (Example) gen.generateValueOfType(Example.class);
            System.out.printf("Example: %s\n", example);

            Cart cart = (Cart) gen.generateValueOfType(Cart.class);
            System.out.printf("Cart: %s\n", cart);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}