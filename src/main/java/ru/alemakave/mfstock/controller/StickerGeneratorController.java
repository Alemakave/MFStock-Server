package ru.alemakave.mfstock.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.alemakave.mfstock.service.IStickerService;

import java.io.*;

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
    public String postNomSerStickerGenerator(@RequestBody String requestBody) {
        return generatorService.postNomSerStickerGenerator(requestBody);
    }

    @PostMapping(path = "/mfstock-generate-nom-sticker", consumes = "application/json")
    public String postNomStickerGenerator(@RequestBody String requestBody) {
        return generatorService.postNomStickerGenerator(requestBody);
    }

    @PostMapping(path = "/mfstock-generate-cell-sticker", consumes = "application/json")
    public String postCellStickerGenerator(@RequestBody String requestBody) {
        return generatorService.postCellStickerGenerator(requestBody);
    }

    @PostMapping(path = "/mfstock-generate-employee-sticker", consumes = "application/json")
    public String postEmployeeStickerGenerator(@RequestBody String requestBody) {
        return generatorService.postEmployeeStickerGenerator(requestBody);
    }

    @PostMapping(path = "/mfstock-generate-order-number-sticker", consumes = "application/json")
    public String postOrderNumberStickerGenerator(@RequestBody String requestBody) {
        return generatorService.postOrderNumberStickerGenerator(requestBody);
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
