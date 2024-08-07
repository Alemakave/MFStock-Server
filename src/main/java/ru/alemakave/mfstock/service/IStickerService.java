package ru.alemakave.mfstock.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.alemakave.mfstock.model.StickerType;

import java.io.IOException;
import java.util.List;

public interface IStickerService {
    String getHomePage() throws IOException;
    String getNomStickerGenerator() throws IOException;
    String getNomSerStickerGenerator() throws IOException;
    String getCellStickerGenerator() throws IOException;
    String getEmployeeStickerGenerator() throws IOException;
    ResponseEntity<String> uploadStickersDataTable(@RequestParam("data-file") MultipartFile file, String currentHtmlCode);
    String getOrderNumberStickerGenerator() throws IOException;
    ResponseEntity<String> getAvailablePrinters() throws Exception;
    ResponseEntity<byte[]> getStickerFile(String uuidStr);
    ResponseEntity<List<String>> postGenerateStickerExcelFile(String requestBody, StickerType stickerType);
    ResponseEntity<Void> postPrintSticker(String requestBody, StickerType stickerType);
}
