package ru.alemakave.mfstock.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.alemakave.mfstock.service.IStickerService;

import java.io.IOException;

@RestController
public class StickerController {
    private final IStickerService generatorService;

    public StickerController(IStickerService generatorService) {
        this.generatorService = generatorService;
    }

    @RequestMapping(path = "/mfstock-generate-nom-sticker")
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

    @GetMapping(params = "mfstock-generate-nom-sticker")
    public String getNomStickerGenerator(String mfstockGenerateNomSticker) {
        return getNomStickerGenerator();
    }

    @GetMapping(params = "mfstock-generate-nom-ser-sticker")
    public String getNomSerStickerGenerator(String mfstockGenerateNomSerSticker) {
        return getNomSerStickerGenerator();
    }

    @PostMapping(path = "/mfstock-generate-nom-ser-sticker", consumes = "application/json")
    public String postNomSerStickerGenerator(@RequestBody String requestBody) {
        return generatorService.postNomSerStickerGenerator(requestBody);
    }

    @PostMapping(path = "/mfstock-generate-nom-sticker", consumes = "application/json")
    public String postNomStickerGenerator(@RequestBody String requestBody) {
        return generatorService.postNomStickerGenerator(requestBody);
    }

    @Deprecated
    @PostMapping(params = {"mfstock-generate-nom-sticker"}, consumes = "application/json")
    public String postNomStickerGenerator(@RequestBody String requestBody, @RequestParam(name="mfstock-generate-nom-sticker") String mfstockGenerateNomSticker) {
        return postNomStickerGenerator(requestBody);
    }

    @Deprecated
    @GetMapping(params = {"mfstock-generate-nom-ser-sticker"}, consumes = "application/json")
    public String postNomSerStickerGenerator(@RequestBody String requestBody, @RequestParam(name="mfstock-generate-nom-ser-sticker") String mfstockGenerateNomSerSticker) {
        return postNomSerStickerGenerator(requestBody);
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exceptionHandler(RuntimeException exception) {
        return exception.getMessage();
    }
}
