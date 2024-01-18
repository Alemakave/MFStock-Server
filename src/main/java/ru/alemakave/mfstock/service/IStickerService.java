package ru.alemakave.mfstock.service;

import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

public interface IStickerService {
    String getHomePage() throws IOException;
    String getNomStickerGenerator() throws IOException;
    String getNomSerStickerGenerator() throws IOException;
    String getCellStickerGenerator() throws IOException;
    String postNomStickerGenerator(@RequestBody String requestBody);
    String postNomSerStickerGenerator(@RequestBody String requestBody);
    String postCellStickerGenerator(@RequestBody String requestBody);
    String getNomPartyGenerator() throws IOException;
    String postNomPartyGenerator(@RequestBody String requestBody);
}
