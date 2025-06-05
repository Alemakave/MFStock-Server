package ru.alemakave.mfstock.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.alemakave.barcode.generator.BarcodeGenerator;
import ru.alemakave.mfstock.dto.StickerDto;
import ru.alemakave.mfstock.dto.StickersDto;
import ru.alemakave.mfstock.generators.html.*;
import ru.alemakave.mfstock.model.json.sticker.*;
import ru.alemakave.mfstock.service.IStickerService;
import ru.alemakave.barcode.ImageType;
import ru.alemakave.slib.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collections;

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

    @Deprecated(forRemoval = true)
    @GetMapping(path = "/mfstock-get-sticker-file")
    public ResponseEntity<byte[]> getStickerFile(@RequestParam("id") String uuidStr) {
        return generatorService.getStickerFile(uuidStr);
    }

    @GetMapping("/mfstock-generate-qr-code")
    public ResponseEntity<byte[]> getGenerateQRCode(@RequestParam("data") String data) {
        try {
            return ResponseEntity.ok()
                    .headers(httpHeaders -> httpHeaders.put("Content-Type", Collections.singletonList(MediaType.IMAGE_PNG_VALUE)))
                    .body(ImageUtils.toByteArray(BarcodeGenerator.generateBufferedImage(BarcodeFormat.QR_CODE, data), ImageType.PNG.name()));
        } catch (IOException | WriterException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/mfstock-generate-barcode")
    public ResponseEntity<byte[]> getGenerateBarcode(@RequestParam BarcodeFormat barcodeFormat,
                                                     @RequestParam("data") String data,
                                                     @RequestParam(name = "width", defaultValue = "-1") int width,
                                                     @RequestParam(name = "height", defaultValue = "-1") int height) {
        try {
            ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
                    .headers(httpHeaders -> httpHeaders.put("Content-Type", Collections.singletonList(MediaType.IMAGE_PNG_VALUE)));

            BufferedImage barcodeBufferedImage;

            if (width == -1 || height == -1) {
                barcodeBufferedImage = BarcodeGenerator.generateBufferedImage(barcodeFormat, data);
            } else {
                barcodeBufferedImage = BarcodeGenerator.generateBufferedImage(barcodeFormat, data, width, height);
            }

            return responseBuilder
                    .body(ImageUtils.toByteArray(barcodeBufferedImage, ImageType.PNG.name()));
        } catch (IOException | WriterException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/mfstock-show-print-nom-stickers")
    public ResponseEntity<String> postShowPrintNomStickers(@RequestBody StickersDto<NomSticker> stickersNomDto) {
        return getShowPrintStickers(stickersNomDto);
    }

    @PostMapping("/mfstock-show-print-nom-ser-stickers")
    public ResponseEntity<String> postShowPrintNomSerStickers(@RequestBody StickersDto<NomSerSticker> stickersNomSerDto) {
        return getShowPrintStickers(stickersNomSerDto);
    }

    @PostMapping("/mfstock-show-print-employee-stickers")
    public ResponseEntity<String> postShowPrintEmployeeStickers(@RequestBody StickersDto<EmployeeSticker> employeeStickerDto) {
        return getShowPrintStickers(employeeStickerDto);
    }

    @PostMapping("/mfstock-show-print-cell-stickers")
    public ResponseEntity<String> postShowPrintCellStickers(@RequestBody StickersDto<CellSticker> cellStickerDto) {
        return getShowPrintStickers(cellStickerDto);
    }

    @PostMapping("/mfstock-show-print-order-number-stickers")
    public ResponseEntity<String> postShowPrintOrderNumberStickers(@RequestBody StickersDto<OrderNumberSticker> orderNumberSticker) {
        return getShowPrintStickers(orderNumberSticker);
    }

    @SneakyThrows
    private ResponseEntity<String> getShowPrintStickers(@RequestBody StickersDto<?> stickersDto) {
        StringBuilder stickers = new StringBuilder();
        NomHtmlStickerGenerator nomHtmlStickerGenerator = new NomHtmlStickerGenerator();
        NomSerHtmlStickerGenerator nomSerHtmlStickerGenerator = new NomSerHtmlStickerGenerator();
        EmployeeHtmlStickerGenerator employeeStickerGenerator = new EmployeeHtmlStickerGenerator();
        CellHtmlStickerGenerator cellStickerGenerator = new CellHtmlStickerGenerator();
        OrderNumberHtmlStickerGenerator orderNumberHtmlStickerGenerator = new OrderNumberHtmlStickerGenerator();

        for (StickerDto<?> stickerDto : stickersDto.getStickers()) {
            if (stickerDto.getType().equalsIgnoreCase("NOM")) {
                stickers.append(new String(nomHtmlStickerGenerator.generate((NomSticker) stickerDto.getSticker())));
            } else if (stickerDto.getType().equalsIgnoreCase("NOM_SERIAL")) {
                stickers.append(new String(nomSerHtmlStickerGenerator.generate((NomSerSticker) stickerDto.getSticker())));
            } else if (stickerDto.getType().equalsIgnoreCase("EMPLOYEE")) {
                stickers.append(new String(employeeStickerGenerator.generate((EmployeeSticker) stickerDto.getSticker())));
            } else if (stickerDto.getType().equalsIgnoreCase("CELL")) {
                stickers.append(new String(cellStickerGenerator.generate((CellSticker) stickerDto.getSticker())));
            } else if (stickerDto.getType().equalsIgnoreCase("ORDER_NUMBER")) {
                stickers.append(new String(orderNumberHtmlStickerGenerator.generate((OrderNumberSticker) stickerDto.getSticker())));
            }
        }

        return ResponseEntity.ok(stickers.toString());
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
