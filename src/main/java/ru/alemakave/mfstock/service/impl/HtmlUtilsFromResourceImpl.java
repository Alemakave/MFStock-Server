package ru.alemakave.mfstock.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import ru.alemakave.mfstock.service.HtmlUtils;

import java.io.BufferedInputStream;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class HtmlUtilsFromResourceImpl implements HtmlUtils {
    private final ConfigurableApplicationContext context;

    @Override
    public String getHtmlPart(String htmlPartPath) {
        return new String(readFile("/html-parts/" + htmlPartPath));
    }

    @Override
    public String getJavascript(String jsPath) {
        return new String(readFile("/js/" + jsPath));
    }

    @Override
    public String getCss(String cssPath) {
        return new String(readFile("/css/" + cssPath));
    }

    @Override
    public byte[] getImage(String imagePath) {
        return readFile("/img/" + imagePath);
    }

    private byte[] readFile(String filePath) {
        try (BufferedInputStream bis = new BufferedInputStream(context.getResource(("classpath:/pages/" + filePath).replace("//", "/")).getInputStream())) {
            return bis.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
