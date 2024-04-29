package ru.alemakave.mfstock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StickerTableException extends HttpStatusCodeException {
    public StickerTableException(String statusText) {
        super(HttpStatus.BAD_REQUEST, statusText);
    }
}
