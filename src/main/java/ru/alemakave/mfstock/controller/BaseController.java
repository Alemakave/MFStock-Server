package ru.alemakave.mfstock.controller;

import org.springframework.context.ConfigurableApplicationContext;
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
}
