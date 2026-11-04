package org.example.generator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Random;

public class Generator {

    public Object generateValueOfType(Class<?> clazz)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (PrimitiveTypeGenerator.isPrimitiveType(clazz)) {
            return PrimitiveTypeGenerator.generateValueOfPrimitiveType(clazz);
        }

        if (clazz == String.class) {
            return StringGenerator.generateString();
        }

        if (clazz.isEnum()) {
            Object[] enumConstants = clazz.getEnumConstants();
            int index = (int) PrimitiveTypeGenerator.generateValueOfPrimitiveType(
                    int.class,
                    0,
                    enumConstants.length - 1
            );
            return enumConstants[index];
        }

        if (clazz.isArray()) {
            return ArrayGenerator.generateArray(clazz, this);
        }

        if (Collection.class.isAssignableFrom(clazz)) {
            return CollectionGenerator.generateCollection(clazz, this);
        }


        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        int randomConstructorIndex = new Random().nextInt(constructors.length);
        Constructor<?> randomConstructor = constructors[randomConstructorIndex];
        return randomConstructor.newInstance(111);
    }

}
