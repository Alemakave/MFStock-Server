package ru.alemakave.mfstock.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IStickerService {
    String getHomePage() throws IOException;
    String getNomStickerGenerator() throws IOException;
    String getNomSerStickerGenerator() throws IOException;
    String getCellStickerGenerator() throws IOException;
    String postNomStickerGenerator(@RequestBody String requestBody);
    String postNomSerStickerGenerator(@RequestBody String requestBody);
    String postCellStickerGenerator(@RequestBody String requestBody);
    String getEmployeeStickerGenerator() throws IOException;
    String postEmployeeStickerGenerator(@RequestBody String requestBody);
    ResponseEntity<String> uploadStickersDataTable(@RequestParam("data-file") MultipartFile file, String currentHtmlCode);
    String getOrderNumberStickerGenerator() throws IOException;
    String postOrderNumberStickerGenerator(@RequestBody String requestBody);
    ResponseEntity<String> getAvailablePrinters() throws Exception;
}
