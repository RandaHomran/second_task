package com.example.sampleproject.exception;

/**
 * This class is served as wrapper of the {@link RuntimeException} class. <br/>
 * The purpose is to be thrown if the resource is unavailable at the runtime <br/>
 * The exception will be thrown if the resource that throws {@link AppConfigRuntimeExceptions} failed at the run time.
 */
public class AppConfigRuntimeExceptions extends RuntimeException {
    public AppConfigRuntimeExceptions(String message) {
        super(message);
    }
}