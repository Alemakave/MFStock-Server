package ru.alemakave.mfstock.service;

public interface HtmlUtils {
    String getHtmlPart(String htmlPartPath);
    String getJavascript(String jsPath);
    String getCss(String cssPath);
    byte[] getImage(String imagePath);
}
