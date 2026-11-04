package org.example.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для конкретных классов, которые могут быть созданы при генерации интерфейса, указанного в {@link #value()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Generatable {
    /**
     * Интерфейс, для создания экземпляра которого может быть использован класс, помеченный этой аннотацией
     */
    Class<?> value();

    /**
     * Если есть несколько классов, помеченных аннотацией, то будет использовать класс с наивысшим приоритетом
     */
    int priority() default 0;
}
