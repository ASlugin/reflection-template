package org.example.generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractTypeGeneratorTest {

    private final Generator generator = new Generator();

    @Test
    void choosesImplementationWithHighestPriority() {
        Object result = AbstractTypeGenerator.generateAbstractType(PriorityContract.class, generator);

        assertInstanceOf(PreferredImplementation.class, result);
    }

    @Test
    void generatesImplementationOfAbstractClass() {
        Object result = AbstractTypeGenerator.generateAbstractType(AbstractFixture.class, generator);

        assertInstanceOf(AbstractFixtureImplementation.class, result);
    }

    @Test
    void throwsWhenNoImplementationIsAnnotatedForRequestedType() {
        assertThrows(IllegalArgumentException.class,
                () -> AbstractTypeGenerator.generateAbstractType(UnimplementedContract.class, generator));
    }

    @Test
    void rejectsConcreteTypes() {
        assertThrows(IllegalArgumentException.class,
                () -> AbstractTypeGenerator.generateAbstractType(String.class, generator));
    }

    public interface PriorityContract {
    }

    @Generatable(value = PriorityContract.class, priority = 10)
    public static class LowerPriorityImplementation implements PriorityContract {
        public LowerPriorityImplementation() {
        }
    }

    @Generatable(value = PriorityContract.class, priority = 1)
    public static class PreferredImplementation implements PriorityContract {
        public PreferredImplementation() {
        }
    }

    public abstract static class AbstractFixture {
    }

    @Generatable(AbstractFixture.class)
    public static class AbstractFixtureImplementation extends AbstractFixture {
        public AbstractFixtureImplementation() {
        }
    }

    public interface UnimplementedContract {
    }
}
