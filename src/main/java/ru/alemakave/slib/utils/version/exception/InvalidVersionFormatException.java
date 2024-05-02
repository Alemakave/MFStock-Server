package ru.alemakave.slib.utils.version.exception;

public class InvalidVersionFormatException extends RuntimeException {
    public InvalidVersionFormatException() {
        super("Invalid version format.");
    }
}
