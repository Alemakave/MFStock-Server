package ru.alemakave.mfstock.controller;

import com.google.common.io.Files;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.IOException;

@RestController
public class BaseController {
    private final ConfigurableApplicationContext context;

    public BaseController(ConfigurableApplicationContext context) {
        this.context = context;
    }

    @GetMapping("/get-style")
    public String getStyle(@RequestParam("name") String name) {
        try {
            BufferedInputStream bui = new BufferedInputStream(context.getResource("classpath:/pages/css/" + name).getInputStream());
            String style = new String(bui.readAllBytes());
            bui.close();
            return style;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get-script")
    public String getScript(@RequestParam("name") String name) {
        try {
            BufferedInputStream bui = new BufferedInputStream(context.getResource("classpath:/pages/js/" + name).getInputStream());
            String style = new String(bui.readAllBytes());
            bui.close();
            return style;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get-image")
    public ResponseEntity<byte[]> getImage(@RequestParam("name") String name) {
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

            BufferedInputStream bis = new BufferedInputStream(context.getResource("classpath:/pages/img/" + name).getInputStream());
            byte[] imageBytes = bis.readAllBytes();
            bis.close();
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get-html-part")
    public String getHtmlPart(@RequestParam("name") String name) {
        try {
            BufferedInputStream bui = new BufferedInputStream(context.getResource("classpath:/pages/html-parts/" + name).getInputStream());
            String style = new String(bui.readAllBytes());
            bui.close();
            return style;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
