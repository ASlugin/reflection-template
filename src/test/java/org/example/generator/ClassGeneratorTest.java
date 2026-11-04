package org.example.generator;

import org.example.classes.BinaryTreeNode;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassGeneratorTest {

    private static final int MAX_TREE_DEPTH = 4;

    private final Generator generator = new Generator();

    @Test
    void generatesObjectUsingConstructorArguments() throws IllegalAccessException {
        IntegerArgumentClass result = assertInstanceOf(IntegerArgumentClass.class,
                ClassGenerator.generateClass(IntegerArgumentClass.class, generator));

        Field valueField = getField(IntegerArgumentClass.class, "value");
        assertEquals(Integer.class, valueField.getType());
        assertInstanceOf(Integer.class, valueField.get(result));
    }

    @Test
    void prefersConstructorWithFewerParameters() throws IllegalAccessException {
        ConstructorChoice result = assertInstanceOf(ConstructorChoice.class,
                ClassGenerator.generateClass(ConstructorChoice.class, generator));

        Field selectedConstructorField = getField(ConstructorChoice.class, "selectedConstructor");
        assertEquals(String.class, selectedConstructorField.getType());
        assertEquals("no-argument", selectedConstructorField.get(result));
    }

    @Test
    void generatesRecursiveClassAndItsNestedFields() throws IllegalAccessException {
        BinaryTreeNode result = assertInstanceOf(BinaryTreeNode.class,
                ClassGenerator.generateClass(BinaryTreeNode.class, generator));

        assertNotNull(result.getData());
        assertNotNull(result.getLeft());
        assertNotNull(result.getRight());
        assertNotSame(result.getLeft(), result.getRight());

        assertBinaryTreeNodeFields(result, 0);
    }

    @Test
    void choosesHighestPriorityAnnotatedImplementationForInterface() {
        Object result = ClassGenerator.generateClass(PriorityContract.class, generator);

        assertInstanceOf(HighPriorityImplementation.class, result);
    }

    @Test
    void generatesAnnotatedImplementationForAbstractClass() {
        Object result = ClassGenerator.generateClass(AbstractFixture.class, generator);

        assertInstanceOf(AbstractFixtureImplementation.class, result);
    }

    @Test
    void throwsWhenNoAnnotatedImplementationExists() {
        assertThrows(IllegalArgumentException.class,
                () -> ClassGenerator.generateClass(UnimplementedContract.class, generator));
        assertThrows(IllegalArgumentException.class,
                () -> ClassGenerator.generateClass(UnimplementedAbstract.class, generator));
    }

    public static class IntegerArgumentClass {
        private final Integer value;

        public IntegerArgumentClass(Integer value) {
            this.value = value;
        }
    }

    public static class ConstructorChoice {
        private final String selectedConstructor;

        public ConstructorChoice() {
            this.selectedConstructor = "no-argument";
        }

        public ConstructorChoice(Integer ignored) {
            this.selectedConstructor = "with-argument";
        }
    }

    public abstract static class AbstractFixture {
        public AbstractFixture() {
        }
    }

    public interface PriorityContract {
    }

    @Generatable(value = PriorityContract.class, priority = 10)
    public static class LowPriorityImplementation implements PriorityContract {
        public LowPriorityImplementation() {
        }
    }

    @Generatable(value = PriorityContract.class, priority = 1)
    public static class HighPriorityImplementation implements PriorityContract {
        public HighPriorityImplementation() {
        }
    }

    @Generatable(value = AbstractFixture.class)
    public static class AbstractFixtureImplementation extends AbstractFixture {
        public AbstractFixtureImplementation() {
        }
    }

    public interface UnimplementedContract {
    }

    public abstract static class UnimplementedAbstract {
    }

    private static void assertBinaryTreeNodeFields(BinaryTreeNode node, int depth) throws IllegalAccessException {
        assertNotNull(node);
        assertTrue(depth < MAX_TREE_DEPTH, "Recursive object generation should stop at the configured depth");

        Field dataField = getField(BinaryTreeNode.class, "data");
        assertEquals(Integer.class, dataField.getType());
        Object dataValue = dataField.get(node);
        assertInstanceOf(Integer.class, dataValue);
        assertEquals(dataValue, node.getData());

        assertChildNodeField(node, "left", depth);
        assertChildNodeField(node, "right", depth);
    }

    private static void assertChildNodeField(BinaryTreeNode node, String fieldName, int depth)
            throws IllegalAccessException {
        Field childField = getField(BinaryTreeNode.class, fieldName);
        assertEquals(BinaryTreeNode.class, childField.getType());

        Object childValue = childField.get(node);
        BinaryTreeNode child = null;
        if (childValue != null) {
            child = assertInstanceOf(BinaryTreeNode.class, childValue);
            assertBinaryTreeNodeFields(child, depth + 1);
        }

        BinaryTreeNode getterValue = "left".equals(fieldName) ? node.getLeft() : node.getRight();
        assertEquals(child, getterValue);
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException exception) {
            throw new AssertionError("Expected field " + fieldName + " on " + clazz.getName(), exception);
        }
    }
}
