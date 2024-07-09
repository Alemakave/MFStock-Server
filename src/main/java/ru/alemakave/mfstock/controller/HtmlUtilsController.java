package ru.alemakave.mfstock.controller;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

@Slf4j
@RestController
public class HtmlUtilsController {
    private final ConfigurableApplicationContext context;

    public HtmlUtilsController(ConfigurableApplicationContext context) {
        this.context = context;
    }

    @Deprecated
    @GetMapping("/get-style")
    public String getStyle(@RequestParam("name") String name) {
        try {
            return getCss(new RequestEntity<>(HttpMethod.GET, new URI("/css/" + name))).getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    @GetMapping("/get-script")
    public String getScript(@RequestParam("name") String name) {
        try {
            return getJavascript(new RequestEntity<>(HttpMethod.GET, new URI("/js/" + name))).getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    @GetMapping("/get-image")
    public ResponseEntity<byte[]> getImage(@RequestParam("name") String name) {
        try {
            return getImage(new RequestEntity<>(HttpMethod.GET, new URI("/img/" + name)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    @GetMapping("/get-html-part")
    public String getHtmlPart(@RequestParam("name") String name) {
        try {
            return getHtmlPart(new RequestEntity<>(HttpMethod.GET, new URI("/html-part/" + name))).getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/html-part/**")
    public ResponseEntity<String> getHtmlPart(RequestEntity<?> requestEntity) {
        log.info("Get html part: " + requestEntity.getUrl().getPath().substring("/html-part".length()));

        String name = requestEntity.getUrl().getPath().substring("/html-part".length());
        try {
            BufferedInputStream bui = new BufferedInputStream(context.getResource("classpath:/pages/html-parts" + name).getInputStream());
            String style = new String(bui.readAllBytes());
            bui.close();
            return ResponseEntity.ok(style);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/css/**")
    public ResponseEntity<String> getCss(RequestEntity<?> requestEntity) {
        log.info("Get CSS: " + requestEntity.getUrl().getPath().substring("/css".length()));

        String name = requestEntity.getUrl().getPath().substring("/css".length());
        try {
            BufferedInputStream bui = new BufferedInputStream(context.getResource("classpath:/pages/css" + name).getInputStream());
            String style = new String(bui.readAllBytes());
            bui.close();
            return ResponseEntity.ok(style);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/img/**")
    public ResponseEntity<byte[]> getImage(RequestEntity<?> requestEntity) {
        log.info("Get image: " + requestEntity.getUrl().getPath().substring("/img".length()));

        String name = requestEntity.getUrl().getPath().substring("/img".length());
        try {
            // TODO: Rework find media type of file
            MediaType mediaType;
            switch (Files.getFileExtension(name).toLowerCase()) {
                case "svg":
                    mediaType = MediaType.valueOf("image/svg+xml");
                    break;
                case "jpg":
                case "jpeg":
                    mediaType = MediaType.IMAGE_JPEG;
                    break;
                case "png":
                    mediaType = MediaType.IMAGE_PNG;
                    break;
                default:
                    mediaType = MediaType.ALL;
                    break;
            }

            BufferedInputStream bis = new BufferedInputStream(context.getResource("classpath:/pages/img" + name).getInputStream());
            byte[] imageBytes = bis.readAllBytes();
            bis.close();
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageBytes);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/js/**")
    public ResponseEntity<String> getJavascript(RequestEntity<?> requestEntity) {
        log.info("Get javascript: " + requestEntity.getUrl().getPath().substring("/js".length()));

        String name = requestEntity.getUrl().getPath().substring("/js".length());
        try {
            BufferedInputStream bui = new BufferedInputStream(context.getResource("classpath:/pages/js" + name).getInputStream());
            String style = new String(bui.readAllBytes());
            bui.close();
            return ResponseEntity.ok(style);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
