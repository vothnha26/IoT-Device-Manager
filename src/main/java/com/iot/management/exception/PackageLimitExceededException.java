package com.iot.management.exception;

public class PackageLimitExceededException extends RuntimeException {
    public PackageLimitExceededException(String message) {
        super(message);
    }
}