package ru.alemakave.mfstock.service;

import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

public interface IStickerService {
    String getNomStickerGenerator() throws IOException;
    String getNomSerStickerGenerator() throws IOException;
    String postNomStickerGenerator(@RequestBody String requestBody);
    String postNomSerStickerGenerator(@RequestBody String requestBody);
}
