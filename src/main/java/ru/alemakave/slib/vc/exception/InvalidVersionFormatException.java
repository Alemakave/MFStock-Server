package ru.alemakave.slib.vc.exception;

public class InvalidVersionFormatException extends RuntimeException {
    public InvalidVersionFormatException(String versionAsString) {
        super(String.format("Invalid version format \"%s\".",versionAsString));
    }
}
