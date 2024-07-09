package ru.alemakave.mfstock.controller;

import com.google.zxing.WriterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.alemakave.mfstock.service.IStickerService;
import ru.alemakave.qr.ImageType;
import ru.alemakave.qr.generator.QRGenerator;
import ru.alemakave.slib.utils.ImageUtils;

import java.io.*;

import static ru.alemakave.mfstock.model.StickerType.*;

@RestController
public class StickerGeneratorController {
    private final IStickerService generatorService;
    private final Logger logger = LogManager.getLogger(getClass());

    public StickerGeneratorController(IStickerService generatorService) {
        this.generatorService = generatorService;
    }

    @GetMapping(path = "/")
    public String getHomePage() {
        try {
            return generatorService.getHomePage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/mfstock-generate-nom-sticker")
    public String getNomStickerGenerator() {
        try {
            return generatorService.getNomStickerGenerator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/mfstock-generate-nom-ser-sticker")
    public String getNomSerStickerGenerator() {
        try {
            return generatorService.getNomSerStickerGenerator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/mfstock-generate-cell-sticker")
    public String getCellStickerGenerator() {
        try {
            return generatorService.getCellStickerGenerator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/mfstock-generate-employee-sticker")
    public String getEmployeeStickerGenerator() {
        try {
            return generatorService.getEmployeeStickerGenerator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/mfstock-generate-order-number-sticker")
    public String getOrderNumberStickerGenerator() {
        try {
            return generatorService.getOrderNumberStickerGenerator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/mfstock-get-available-printers")
    public ResponseEntity<String> getAvailablePrinters() {
        try {
            return generatorService.getAvailablePrinters();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(path = "/mfstock-generate-nom-ser-sticker", consumes = "application/json")
    public void postNomSerStickerGenerator(@RequestBody String requestBody) {
        generatorService.postPrintSticker(requestBody, NOM_SERIAL);
    }

    @PostMapping(path = "/mfstock-generate-nom-sticker", consumes = "application/json")
    public void postNomStickerGenerator(@RequestBody String requestBody) {
        generatorService.postPrintSticker(requestBody, NOM);
    }

    @PostMapping(path = "/mfstock-generate-cell-sticker", consumes = "application/json")
    public void postCellStickerGenerator(@RequestBody String requestBody) {
        generatorService.postPrintSticker(requestBody, CELL);
    }

    @PostMapping(path = "/mfstock-generate-employee-sticker", consumes = "application/json")
    public void postEmployeeStickerGenerator(@RequestBody String requestBody) {
        generatorService.postPrintSticker(requestBody, EMPLOYEE);
    }

    @PostMapping(path = "/mfstock-generate-order-number-sticker", consumes = "application/json")
    public void postOrderNumberStickerGenerator(@RequestBody String requestBody) {
        generatorService.postPrintSticker(requestBody, ORDER_NUMBER);
    }

    @PostMapping(path = "/mfstock-generate-cell-sticker")
    public ResponseEntity<String> postCellStickerGenerator(@RequestParam("data-file") MultipartFile file) {
        return generatorService.uploadStickersDataTable(file, getCellStickerGenerator());
    }

    @PostMapping(path = "/mfstock-generate-nom-sticker")
    public ResponseEntity<String> postNomStickerUploadFile(@RequestParam("data-file") MultipartFile file) {
        return generatorService.uploadStickersDataTable(file, getNomStickerGenerator());
    }

    @PostMapping(path = "/mfstock-generate-nom-ser-sticker")
    public ResponseEntity<String> postNomSerStickerUploadFile(@RequestParam("data-file") MultipartFile file) {
        return generatorService.uploadStickersDataTable(file, getNomSerStickerGenerator());
    }

    @GetMapping(path = "/mfstock-get-sticker-file")
    public ResponseEntity<byte[]> getStickerFile(@RequestParam("id") String uuidStr) {
        return generatorService.getStickerFile(uuidStr);
    }

    @GetMapping("/mfstock-generate-qr-code")
    public ResponseEntity<byte[]> getGenerateQRCode(@RequestParam("data") String data) {
        try {
            return ResponseEntity.ok(ImageUtils.toByteArray(QRGenerator.generateToBufferedImage(data), ImageType.PNG.name()));
        } catch (IOException | WriterException e) {
            throw new RuntimeException(e);
        }
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exceptionHandler(RuntimeException exception) {
        logger.error(exception);
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            logger.error("\t" + stackTraceElement);
        }
        //TODO: Добавить страницу с ошибкой
        return String.format("<body style=\"display: flex;flex-direction: column;\">\n" +
                             "   <div>%s</div>\n" +
                             "   <a style=\"margin: 10px;\" href>Назад</a>\n" +
                             "</body>", exception.getMessage());
    }
}
