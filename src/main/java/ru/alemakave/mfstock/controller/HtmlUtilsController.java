package ru.alemakave.mfstock.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alemakave.mfstock.service.HtmlUtils;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HtmlUtilsController {
    private final HtmlUtils htmlUtils;

    @GetMapping("/html-part/**")
    public ResponseEntity<String> getHtmlPart(RequestEntity<?> requestEntity) {
        log.info("Get html part: " + requestEntity.getUrl().getPath().substring("/html-part".length()));

        String filename = requestEntity.getUrl().getPath().substring("/html-part".length());
        MediaType fileMediaType = MediaTypeFactory.getMediaType(filename).orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(fileMediaType)
                .body(htmlUtils.getHtmlPart(filename));
    }

    @GetMapping("/css/**")
    public ResponseEntity<String> getCss(RequestEntity<?> requestEntity) {
        log.info("Get CSS: " + requestEntity.getUrl().getPath().substring("/css".length()));

        String filename = requestEntity.getUrl().getPath().substring("/css".length());
        MediaType fileMediaType = MediaTypeFactory.getMediaType(filename).orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(fileMediaType)
                .body(htmlUtils.getCss(filename));
    }

    @GetMapping("/img/**")
    public ResponseEntity<byte[]> getImage(RequestEntity<?> requestEntity) {
        log.info("Get image: " + requestEntity.getUrl().getPath().substring("/img".length()));

        String filename = requestEntity.getUrl().getPath().substring("/img".length());
        MediaType fileMediaType = MediaTypeFactory.getMediaType(filename).orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(fileMediaType)
                .body(htmlUtils.getImage(filename));
    }

    @GetMapping("/js/**")
    public ResponseEntity<String> getJavascript(RequestEntity<?> requestEntity) {
        log.info("Get javascript: " + requestEntity.getUrl().getPath().substring("/js".length()));

        String filename = requestEntity.getUrl().getPath().substring("/js".length());
        MediaType fileMediaType = MediaTypeFactory.getMediaType(filename).orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(fileMediaType)
                .body(htmlUtils.getJavascript(filename));
    }
}
