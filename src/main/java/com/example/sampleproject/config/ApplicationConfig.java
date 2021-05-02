package com.example.sampleproject.config;

import com.example.sampleproject.exception.AppConfigExceptions;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.util.Properties;

/**
 * The class is intended to be used as default config file <br/>
 * where developer can have access to the important configurations such as:
 * <ul>
 *     <li> DB name,</li>
 *     <li> DB port,</li>
 * </ul>
 */
@Log4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationConfig extends BaseConfig {
    private static Properties properties;

    /**
     * Binds application.properties to the current class.
     *
     * @return {@link Properties}
     */
    static Properties getProperties() throws AppConfigExceptions {

        if (properties == null) {
            properties = readProperties("application", AppConfigExceptions.class, ApplicationConfig.class);
        }
        return properties;
    }

    /**
     * Standard getter for host of the database.
     *
     * @return string value of the host
     */
    public static String getDbHost() throws AppConfigExceptions {
        return getProperties().getProperty("db.host");
    }

    /**
     * Standard getter for the host port.
     *
     * @return port parsed as integer
     */
    public static int getDbPort() throws AppConfigExceptions {
        String port;

        try {
            port = getProperties().getProperty("db.port");
        } catch (IllegalArgumentException e) {
            throw new AppConfigExceptions("The DB port not found - " + e.getLocalizedMessage());
        }

        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new AppConfigExceptions("The port isn't convertible to integer - " + e.getLocalizedMessage());
        }
    }

    /**
     * Standard getter for DB name.
     *
     * @return DB name.
     * @throws AppConfigExceptions when can't find the property and gives detailed message about the failure
     */
    public static String getDbName() throws AppConfigExceptions {
        try {
            return getProperties().getProperty("db.name");
        } catch (IllegalArgumentException e) {
            throw new AppConfigExceptions("The DB name not found - " + e.getLocalizedMessage());
        }
    }

    /**
     * Wrapper for {@link #getDbName()}: suppress if fails.
     *
     * @return DB name
     */
    public static String safeGetDbName() {
        return Try.of(ApplicationConfig::getDbName).get();
    }


    /**
     * Standard getter for user set name.<br/>
     * Used to store info about requested servers by user.
     *
     * @return the set name
     * @throws AppConfigExceptions when can't find the property and gives detailed message about the failure
     */
    public static String getUserSetName() throws AppConfigExceptions {
        try {
            return getProperties().getProperty("user.set");
        } catch (IllegalArgumentException e) {
            throw new AppConfigExceptions("The user set name not found - " + e.getLocalizedMessage());
        }
    }

    /**
     * Wrapper for {@link #getUserSetName()}: suppress if fails.
     *
     * @return the set name
     */
    public static String safeGetUserSetName() {
        return Try.of(ApplicationConfig::getUserSetName).get();
    }
}