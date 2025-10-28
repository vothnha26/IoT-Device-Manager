package com.iot.management.exception;

public class PackageExpiredException extends RuntimeException {
    public PackageExpiredException(String message) {
        super(message);
    }
}