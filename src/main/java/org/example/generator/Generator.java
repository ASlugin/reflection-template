package org.example.generator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Map;

public class Generator {

    public Object generateValueOfType(Class<?> clazz)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return generateValueOfType((Type) clazz, 0);
    }

    Object generateValueOfType(Class<?> clazz, int depth)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return generateValueOfType((Type) clazz, depth);
    }

    Object generateValueOfType(Type type, int depth)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (type == null) {
            throw new IllegalArgumentException("Type must not be null");
        }

        if (type instanceof ParameterizedType parameterizedType) {
            if (!(parameterizedType.getRawType() instanceof Class<?> rawClass)) {
                throw new IllegalArgumentException("Unsupported parameterized type: " + type.getTypeName());
            }

            if (Collection.class.isAssignableFrom(rawClass)) {
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                Type elementType = typeArguments.length == 1
                        ? resolveTypeArgument(typeArguments[0])
                        : Byte.class;
                return CollectionGenerator.generateCollection(rawClass, this, elementType, depth);
            }

            return generateValueOfType(rawClass, depth);
        }

        if (type instanceof WildcardType wildcardType) {
            return generateValueOfType(resolveTypeArgument(wildcardType), depth);
        }

        if (type instanceof TypeVariable<?> typeVariable) {
            return generateValueOfType(resolveTypeArgument(typeVariable), depth);
        }

        if (!(type instanceof Class<?> clazz)) {
            throw new IllegalArgumentException("Unsupported type: " + type.getTypeName());
        }

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

        if (Map.class.isAssignableFrom(clazz)) {
            return MapGenerator.generateMap(clazz, this);
        }

        return ClassGenerator.generateClass(clazz, this, depth);
    }

    private static Type resolveTypeArgument(Type type) {
        if (type instanceof WildcardType wildcardType) {
            Type[] lowerBounds = wildcardType.getLowerBounds();
            if (lowerBounds.length > 0) {
                return resolveTypeArgument(lowerBounds[0]);
            }

            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length > 0 && upperBounds[0] != Object.class) {
                return resolveTypeArgument(upperBounds[0]);
            }
            return Byte.class;
        }

        if (type instanceof TypeVariable<?> typeVariable) {
            Type[] bounds = typeVariable.getBounds();
            if (bounds.length > 0 && bounds[0] != Object.class) {
                return resolveTypeArgument(bounds[0]);
            }
            return Byte.class;
        }

        return type;
    }

}
