package ru.alemakave.barcode.exception;

public class UnsupportedBarcode extends RuntimeException {
    public UnsupportedBarcode(String message) {
        super(message);
    }
}
