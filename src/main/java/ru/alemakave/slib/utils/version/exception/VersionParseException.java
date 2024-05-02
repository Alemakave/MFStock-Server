package ru.alemakave.slib.utils.version.exception;

public class VersionParseException extends RuntimeException {
    public VersionParseException(String version) {
        super(String.format("Failed to parse version \"%s\"", version));
    }
}
