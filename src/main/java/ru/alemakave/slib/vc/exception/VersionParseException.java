package ru.alemakave.slib.vc.exception;

public class VersionParseException extends RuntimeException {
    public VersionParseException(String version) {
        super(String.format("Failed to parse version \"%s\"", version));
    }
}
