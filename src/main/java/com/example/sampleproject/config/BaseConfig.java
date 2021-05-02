package com.example.sampleproject.config;

import io.vavr.control.Try;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * All methods that are shared in all configuration classes should be separated here.
 */
public abstract class BaseConfig {

    /**
     * The method reads any '.properties' that is located in {@link resources} folder.
     *
     * @param resourceName   is a properties name without extension.
     * @param exceptionClass the exception that should be thrown if something goes wrong
     * @param config         class that should be mapped to the given 'property' file
     * @param <T>            the type of exceptionClass
     * @param <A>            the type of config
     * @return the {@link Properties} instance that have been load the .properties' file
     * @throws T custom checked exception
     */
    static <T extends Exception, A> Properties readProperties(String resourceName, Class<T> exceptionClass,
                                                              Class<A> config) throws T {

        Properties properties = new Properties();
        InputStream stream = config
                .getClassLoader()
                .getResourceAsStream(resourceName.concat(".properties"));

        try {
            properties.load(stream);
        } catch (IOException e) {
            throw Try.of(() -> exceptionClass.getConstructor(String.class)
                    .newInstance(String.format("Unable to load '%s' - ", resourceName)
                            + e.getLocalizedMessage()))
                    .get();
        } finally {
            Optional.ofNullable(stream)
                    .ifPresent(st -> Try.run(st::close));
        }
        return properties;
    }
}
