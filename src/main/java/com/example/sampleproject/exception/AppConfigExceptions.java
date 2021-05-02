package com.example.sampleproject.exception;
/**
 * This class is served as wrapper of the {@link Exception} class. <br/>
 * The purpose is to provide clear messages to the developer to understand the real root cause of the failure. <br/>
 * The exception will be thrown if the asked resource:
 * <ul>
 *     <li> Doesn't exists </li>
 *     <li> Is not convertible to the given type </li>
 * </ul>
 */
public class AppConfigExceptions extends Exception {
    public AppConfigExceptions(String message) {
        super(message);
    }
}