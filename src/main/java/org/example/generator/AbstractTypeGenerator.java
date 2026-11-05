package org.example.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class AbstractTypeGenerator {

    private AbstractTypeGenerator() {}

    public static Object generateAbstractType(Class<?> clazz, Generator valueGenerator) {
        return generateAbstractType(clazz, valueGenerator, 0);
    }

    static Object generateAbstractType(Class<?> clazz, Generator valueGenerator, int depth) {
        if (clazz == null) {
            throw new IllegalArgumentException("Requested type must not be null");
        }
        if (!Modifier.isInterface(clazz.getModifiers()) && !Modifier.isAbstract(clazz.getModifiers())) {
            throw new IllegalArgumentException(clazz.getName() + " is not an interface or abstract class");
        }

        List<Class<?>> implementations = findAnnotatedImplementations(clazz);
        implementations.sort(Comparator
                .comparingInt((Class<?> implementation) -> implementation.getAnnotation(Generatable.class).priority())
                .thenComparing(Class::getName));

        if (implementations.isEmpty()) {
            throw new IllegalArgumentException("No @Generatable implementation found for " + clazz.getName());
        }

        Throwable lastFailure = null;
        for (Class<?> implementation : implementations) {
            try {
                return ClassGenerator.generateConcreteClass(implementation, valueGenerator, depth);
            } catch (RuntimeException exception) {
                lastFailure = exception;
            }
        }

        throw new IllegalArgumentException(
                "Could not create an @Generatable implementation for " + clazz.getName(),
                lastFailure
        );
    }

    private static List<Class<?>> findAnnotatedImplementations(Class<?> clazz) {
        String packageName = clazz.getPackageName();
        String packagePath = packageName.replace('.', '/');
        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }

        Set<String> classNames = findClassNames(packagePath, packageName, classLoader);
        List<Class<?>> implementations = new ArrayList<>();
        for (String className : classNames) {
            Class<?> candidate;
            try {
                candidate = Class.forName(className, false, classLoader);
            } catch (ClassNotFoundException | LinkageError exception) {
                continue;
            }

            Generatable annotation = candidate.getAnnotation(Generatable.class);
            if (annotation == null ||
                    !annotation.value().isAssignableFrom(clazz) ||
                    !clazz.isAssignableFrom(candidate) ||
                    candidate.isInterface() ||
                    Modifier.isAbstract(candidate.getModifiers())) {
                continue;
            }
            implementations.add(candidate);
        }
        return implementations;
    }

    private static Set<String> findClassNames(String packagePath, String packageName, ClassLoader classLoader) {
        Set<String> classNames = new HashSet<>();
        try {
            Enumeration<URL> resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                addClassNamesFromDirectory(resource, packagePath, classNames);

            }
        } catch (IOException | URISyntaxException exception) {
            throw new IllegalStateException("Could not scan package " + packageName, exception);
        }
        return classNames;
    }

    private static void addClassNamesFromDirectory(URL resource, String packagePath, Set<String> classNames)
            throws IOException, URISyntaxException {
        Path root = Paths.get(resource.toURI());
        try (Stream<Path> files = Files.walk(root)) {
            files.filter(Files::isRegularFile)
                    .map(root::relativize)
                    .map(Path::toString)
                    .map(path -> path.replace(File.separatorChar, '/'))
                    .filter(path -> path.endsWith(".class"))
                    .map(path -> packagePath.isEmpty() ? path : packagePath + "/" + path)
                    .map(AbstractTypeGenerator::toClassName)
                    .filter(className -> !className.isEmpty())
                    .forEach(classNames::add);
        }
    }

    private static String toClassName(String classFilePath) {
        String normalizedPath = classFilePath.replace(File.separatorChar, '/');
        return normalizedPath
                .substring(0, normalizedPath.length() - ".class".length())
                .replace('/', '.');
    }
}
